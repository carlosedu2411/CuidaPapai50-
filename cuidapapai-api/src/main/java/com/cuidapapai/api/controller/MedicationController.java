package com.cuidapapai.api.controller;

import com.cuidapapai.api.dto.MedicationResponse;
import com.cuidapapai.api.dto.TakeMedicationRequest;
import com.cuidapapai.api.dto.TakeMedicationResponse;
import com.cuidapapai.api.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping("/today")
    public ResponseEntity<List<MedicationResponse>> getTodayMedications() {
        return ResponseEntity.ok(medicationService.getTodayMedications());
    }

    @PatchMapping("/{id}/take")
    public ResponseEntity<TakeMedicationResponse> takeMedication(
            @PathVariable("id") String id,
            @RequestBody(required = false) TakeMedicationRequest request
    ) {
        return ResponseEntity.ok(medicationService.markAsTaken(id, request));
    }
}
