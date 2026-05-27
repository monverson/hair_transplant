package com.hairtrack.transformation.repository;

import com.hairtrack.transformation.entity.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);

    boolean existsByUserIdAndMilestoneDays(UUID userId, Integer milestoneDays);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId AND n.type = :type")
    int deleteByUserIdAndType(UUID userId, Notification.NotificationType type);
}