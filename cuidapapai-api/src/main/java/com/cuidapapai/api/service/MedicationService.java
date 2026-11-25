package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.CreateMedicationRequest;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public List<MedicationResponse> getTodayMedications(String userId) {
        LocalDate today = LocalDate.now();
        List<Medication> medications = medicationRepository.findByUserIdAndScheduledDateOrderByScheduledTimeAsc(userId, today);

        return medications.stream()
                .map(this::toResponse)
                .toList();
    }

    public MedicationResponse createMedication(String userId, CreateMedicationRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do medicamento é obrigatório.");
        }
        if (request.getDosage() == null || request.getDosage().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A dosagem é obrigatória.");
        }

        String time = request.getTime() != null && !request.getTime().isBlank()
                ? request.getTime()
                : (request.getScheduledTime() != null && !request.getScheduledTime().isBlank()
                    ? request.getScheduledTime()
                    : "09:00");

        LocalDate date = request.getScheduledDate() != null ? request.getScheduledDate() : LocalDate.now();
        String medId = "med_" + UUID.randomUUID().toString().substring(0, 8);

        Medication medication = Medication.builder()
                .id(medId)
                .userId(userId)
                .name(request.getName().trim())
                .dosage(request.getDosage().trim())
                .scheduledTime(time)
                .instructions(request.getInstructions())
                .isTaken(false)
                .takenAt(null)
                .scheduledDate(date)
                .build();

        medicationRepository.save(medication);

        return toResponse(medication);
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
