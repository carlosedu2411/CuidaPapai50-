package com.cuidapapai.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 100)
    private String relation;

    @Column(nullable = false, length = 5)
    private String initials;

    @Column(name = "is_professional")
    @Builder.Default
    private Boolean isProfessional = false;

    @Column(name = "is_available_today")
    private Boolean isAvailableToday;

    @Column(nullable = false, length = 25)
    private String phone;

    @Column(name = "avatar_color_hex", nullable = false, length = 7)
    private String avatarColorHex;
}
