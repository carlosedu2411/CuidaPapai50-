package com.cuidapapai.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}") String code,
        @NotBlank @Size(max = 255) String newPassword
) {
}
