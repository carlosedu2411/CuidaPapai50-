package com.cuidapapai.api.controller;

import com.cuidapapai.api.dto.PasswordRecoveryRequest;
import com.cuidapapai.api.dto.PasswordResetRequest;
import com.cuidapapai.api.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/password-recovery")
@RequiredArgsConstructor
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping
    public ResponseEntity<Map<String, String>> sendCode(@Valid @RequestBody PasswordRecoveryRequest request) {
        passwordRecoveryService.sendRecoveryCode(request);
        return ResponseEntity.ok(Map.of(
                "message",
                "Se o e-mail estiver cadastrado, enviaremos um código de recuperação."
        ));
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordRecoveryService.resetPassword(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
