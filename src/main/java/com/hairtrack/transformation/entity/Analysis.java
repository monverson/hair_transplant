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
@Table(name = "analyses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    private Double densityScore;

    private Double hairlineScore;

    private Double crownScore;

    private Double templeScore;

    @Enumerated(EnumType.STRING)
    private ShockLossStatus shockLossStatus;

    private String stageAssessment;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(columnDefinition = "TEXT")
    private String rawAnalysis;

    private Integer monthsPostOp;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum ShockLossStatus {
        NONE, ACTIVE, RESOLVING, COMPLETED
    }
}
