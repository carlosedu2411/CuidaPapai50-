package com.cuidapapai.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "event_datetime", nullable = false)
    private OffsetDateTime eventDatetime;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex;
}
