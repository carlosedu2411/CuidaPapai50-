package com.cuidapapai.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "password_recovery_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordRecoveryCode {

    @Id
    @Column(length = 150)
    private String email;

    @Column(name = "code_hash", nullable = false, length = 64)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "failed_attempts", nullable = false)
    @Builder.Default
    private int failedAttempts = 0;
}
