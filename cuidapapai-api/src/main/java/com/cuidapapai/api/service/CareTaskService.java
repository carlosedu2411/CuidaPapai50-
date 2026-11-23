package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.CareTaskResponse;
import com.cuidapapai.api.dto.ToggleCareTaskRequest;
import com.cuidapapai.api.dto.ToggleCareTaskResponse;
import com.cuidapapai.api.model.CareTask;
import com.cuidapapai.api.repository.CareTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareTaskService {

    private final CareTaskRepository careTaskRepository;

    public List<CareTaskResponse> getTodayTasks() {
        LocalDate today = LocalDate.now();
        List<CareTask> tasks = careTaskRepository.findByTaskDateOrderByIdAsc(today);

        if (tasks.isEmpty()) {
            tasks = careTaskRepository.findAll();
        }

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }

    public ToggleCareTaskResponse toggleTask(String id, ToggleCareTaskRequest request) {
        CareTask task = careTaskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada com id: " + id));

        if (request != null) {
            if (request.getIsCompleted() != null) {
                task.setIsCompleted(request.getIsCompleted());
            }

            if (request.getValue() != null && !request.getValue().isBlank()) {
                task.setValue(request.getValue());
                task.setSubtitle("Registrado hoje (" + request.getValue() + ")");
            }

            if (request.getCompletedAt() != null) {
                task.setCompletedAt(request.getCompletedAt());
            } else if (Boolean.TRUE.equals(task.getIsCompleted()) && task.getCompletedAt() == null) {
                task.setCompletedAt(OffsetDateTime.now());
            }
        }

        careTaskRepository.save(task);

        return ToggleCareTaskResponse.builder()
                .success(true)
                .updatedSubtitle(task.getSubtitle())
                .build();
    }

    private CareTaskResponse toResponse(CareTask task) {
        return CareTaskResponse.builder()
                .id(task.getId())
                .category(task.getCategory())
                .title(task.getTitle())
                .subtitle(task.getSubtitle())
                .isCompleted(Boolean.TRUE.equals(task.getIsCompleted()))
                .colorHex(task.getColorHex())
                .build();
    }
}
