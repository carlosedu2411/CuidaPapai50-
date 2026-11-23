package com.cuidapapai.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "medications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String dosage;

    @Column(name = "scheduled_time", nullable = false, length = 5)
    private String scheduledTime;

    @Column(length = 255)
    private String instructions;

    @Column(name = "is_taken")
    @Builder.Default
    private Boolean isTaken = false;

    @Column(name = "taken_at")
    private OffsetDateTime takenAt;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;
}
