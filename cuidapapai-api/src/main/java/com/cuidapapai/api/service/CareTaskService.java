package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.CareTaskResponse;
import com.cuidapapai.api.dto.CreateCareTaskRequest;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CareTaskService {

    private final CareTaskRepository careTaskRepository;

    public List<CareTaskResponse> getTodayTasks(String userId) {
        LocalDate today = LocalDate.now();
        List<CareTask> tasks;
        if (userId != null && !userId.isBlank()) {
            tasks = careTaskRepository.findByUserIdAndTaskDateOrderByIdAsc(userId, today);
        } else {
            tasks = careTaskRepository.findByTaskDateOrderByIdAsc(today);
        }

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }

    public CareTaskResponse createCareTask(String userId, CreateCareTaskRequest request) {
        if (request == null || request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título da tarefa é obrigatório.");
        }

        String taskId = "tsk_" + UUID.randomUUID().toString().substring(0, 8);
        LocalDate taskDate = request.getTaskDate() != null ? request.getTaskDate() : LocalDate.now();
        String colorHex = (request.getColorHex() != null && !request.getColorHex().isBlank()) ? request.getColorHex() : "#3B82F6";
        String category = (request.getCategory() != null && !request.getCategory().isBlank()) ? request.getCategory() : "Geral";

        CareTask task = CareTask.builder()
                .id(taskId)
                .userId(userId)
                .category(category)
                .title(request.getTitle().trim())
                .subtitle(request.getSubtitle())
                .isCompleted(false)
                .colorHex(colorHex)
                .taskDate(taskDate)
                .build();

        CareTask saved = careTaskRepository.save(task);
        return toResponse(saved);
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
