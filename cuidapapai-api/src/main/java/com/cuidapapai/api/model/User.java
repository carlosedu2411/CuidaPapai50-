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
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "short_name", nullable = false, length = 80)
    private String shortName;

    @Column(length = 30)
    @Builder.Default
    private String role = "elderly";

    @Column(length = 20)
    @Builder.Default
    private String theme = "light";

    @Column(name = "font_size", length = 20)
    @Builder.Default
    private String fontSize = "large";
}
