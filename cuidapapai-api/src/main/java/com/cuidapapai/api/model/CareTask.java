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
@Table(name = "care_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareTask {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(length = 100)
    private String value;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex;

    @Column(name = "task_date", nullable = false)
    private LocalDate taskDate;
}
