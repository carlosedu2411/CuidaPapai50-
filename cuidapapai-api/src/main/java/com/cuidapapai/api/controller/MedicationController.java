package com.cuidapapai.api.controller;

import com.cuidapapai.api.config.UserResolver;
import com.cuidapapai.api.dto.CreateMedicationRequest;
import com.cuidapapai.api.dto.MedicationResponse;
import com.cuidapapai.api.dto.TakeMedicationRequest;
import com.cuidapapai.api.dto.TakeMedicationResponse;
import com.cuidapapai.api.service.MedicationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;
    private final UserResolver userResolver;

    @GetMapping("/today")
    public ResponseEntity<List<MedicationResponse>> getTodayMedications(HttpServletRequest request) {
        String userId = userResolver.resolveUserId(request);
        return ResponseEntity.ok(medicationService.getTodayMedications(userId));
    }

    @PostMapping
    public ResponseEntity<MedicationResponse> createMedication(
            HttpServletRequest request,
            @RequestBody CreateMedicationRequest body
    ) {
        String userId = userResolver.resolveUserId(request);
        MedicationResponse created = medicationService.createMedication(userId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/take")
    public ResponseEntity<TakeMedicationResponse> takeMedication(
            @PathVariable("id") String id,
            @RequestBody(required = false) TakeMedicationRequest request
    ) {
        return ResponseEntity.ok(medicationService.markAsTaken(id, request));
    }
}
