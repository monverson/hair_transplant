package com.hairtrack.transformation.service;

import com.hairtrack.transformation.entity.Notification;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.exception.AccessDeniedException;
import com.hairtrack.transformation.exception.ResourceNotFoundException;
import com.hairtrack.transformation.repository.NotificationRepository;
import com.hairtrack.transformation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Kullanıcının ekim tarihine göre hangi milestone'lara ulaştığını kontrol eder,
     * henüz oluşturulmamış olanları üretir.
     */
    @Transactional
    public List<Notification> checkAndCreateMilestones(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getTransplantDate() == null) {
            log.info("User {} has no transplant date, skipping milestones", userId);
            return List.of();
        }

        int daysSinceTransplant = (int) ChronoUnit.DAYS.between(user.getTransplantDate(), LocalDate.now());

        List<Notification> created = new ArrayList<>();

        for (MilestoneDefinitions.Milestone milestone : MilestoneDefinitions.MILESTONES) {
            // Geçtiğimiz milestone'lar
            if (daysSinceTransplant >= milestone.getDayMark()) {
                // Daha önce oluşturulmamışsa
                boolean exists = notificationRepository.existsByUserIdAndMilestoneDays(
                        userId, milestone.getDayMark()
                );

                if (!exists) {
                    Notification notification = Notification.builder()
                            .user(user)
                            .type(Notification.NotificationType.MILESTONE)
                            .milestoneDays(milestone.getDayMark())
                            .titleTr(milestone.getTitleTr())
                            .titleEn(milestone.getTitleEn())
                            .messageTr(milestone.getMessageTr())
                            .messageEn(milestone.getMessageEn())
                            .read(false)
                            .build();

                    created.add(notificationRepository.save(notification));
                    log.info("Created milestone notification - userId: {}, day: {}", userId, milestone.getDayMark());
                }
            }
        }

        return created;
    }

    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    public Notification markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found" + userId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have access");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(UUID userId) {
        List<Notification> unread = getUnreadNotifications(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}