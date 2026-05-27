package com.hairtrack.transformation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Integer milestoneDays;       // Hangi günde tetiklendi (örn: 11, 30, 90)

    private String titleTr;
    private String titleEn;

    @Column(columnDefinition = "TEXT")
    private String messageTr;

    @Column(columnDefinition = "TEXT")
    private String messageEn;

    @Builder.Default
    private Boolean read = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum NotificationType {
        MILESTONE,           // Aşama bildirimleri (gün/ay bazlı)
        CHECK_IN_REMINDER,   // Haftalık check-in hatırlatma
        TIP,                 // Genel ipuçları
        ANALYSIS_READY       // Yeni analiz hazır
    }
}