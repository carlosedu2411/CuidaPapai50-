package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.MedicationResponse;
import com.cuidapapai.api.dto.TakeMedicationRequest;
import com.cuidapapai.api.dto.TakeMedicationResponse;
import com.cuidapapai.api.model.Medication;
import com.cuidapapai.api.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public List<MedicationResponse> getTodayMedications() {
        LocalDate today = LocalDate.now();
        List<Medication> medications = medicationRepository.findByScheduledDateOrderByScheduledTimeAsc(today);

        if (medications.isEmpty()) {
            // Fallback caso não haja remédios para o dia exato
            medications = medicationRepository.findAll();
        }

        return medications.stream()
                .map(this::toResponse)
                .toList();
    }

    public TakeMedicationResponse markAsTaken(String id, TakeMedicationRequest request) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicamento não encontrado com id: " + id));

        medication.setIsTaken(true);
        medication.setTakenAt(request != null && request.getTakenAt() != null
                ? request.getTakenAt()
                : OffsetDateTime.now());

        medicationRepository.save(medication);

        return TakeMedicationResponse.builder()
                .success(true)
                .message("Medicamento registrado com sucesso.")
                .build();
    }

    private MedicationResponse toResponse(Medication med) {
        return MedicationResponse.builder()
                .id(med.getId())
                .name(med.getName())
                .dosage(med.getDosage())
                .time(med.getScheduledTime())
                .instructions(med.getInstructions())
                .isTaken(Boolean.TRUE.equals(med.getIsTaken()))
                .build();
    }
}
