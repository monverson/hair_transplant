package com.hairtrack.transformation.service;

import com.hairtrack.transformation.entity.Photo;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.exception.AccessDeniedException;
import com.hairtrack.transformation.exception.ResourceNotFoundException;
import com.hairtrack.transformation.repository.PhotoRepository;
import com.hairtrack.transformation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    public Photo uploadPhoto(UUID userId, MultipartFile file, Photo.PhotoAngle angle) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        String storageKey = storageService.uploadPhoto(file, userId);

        Photo photo = Photo.builder()
                .user(user)
                .storageUrl(storageKey)
                .angle(angle)
                .build();

        Photo saved = photoRepository.save(photo);
        log.info("Photo saved - id: {}, userId: {}", saved.getId(), userId);
        return saved;
    }

    public List<Photo> getUserPhotos(UUID userId) {
        return photoRepository.findByUserIdOrderByTakenAtDesc(userId);
    }

    public Photo getPhotoById(UUID photoId, UUID userId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found: " + photoId));

        if (!photo.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have access to this photo");
        }

        return photo;
    }

    public String getPhotoSignedUrl(UUID photoId, UUID userId) {
        Photo photo = getPhotoById(photoId, userId);
        return storageService.getSignedUrl(photo.getStorageUrl());
    }

    public void deletePhoto(UUID photoId, UUID userId) {
        Photo photo = getPhotoById(photoId, userId);
        storageService.deletePhoto(photo.getStorageUrl());
        photoRepository.delete(photo);
    }
}