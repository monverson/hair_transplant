package com.hairtrack.transformation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String name;

    // Saç ekimi bilgileri
    private LocalDate transplantDate;

    private Integer totalGrafts;

    @Enumerated(EnumType.STRING)
    private TransplantMethod method;

    private String zones;        // "hairline,crown,temples"

    private String medications;  // "minoxidil,finasteride"

    private Boolean previousSession;

    private Integer previousGrafts;

    // Subscription
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;

    private LocalDateTime subscriptionExpiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum TransplantMethod {
        DHI, FUE, SAPPHIRE_FUE, OTHER
    }

    public enum SubscriptionStatus {
        FREE, TRIAL, ACTIVE, EXPIRED
    }
}
