package com.cuidapapai.api.controller;

import com.cuidapapai.api.dto.CareTaskResponse;
import com.cuidapapai.api.dto.ToggleCareTaskRequest;
import com.cuidapapai.api.dto.ToggleCareTaskResponse;
import com.cuidapapai.api.service.CareTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/care-tasks")
@RequiredArgsConstructor
public class CareTaskController {

    private final CareTaskService careTaskService;

    @GetMapping("/today")
    public ResponseEntity<List<CareTaskResponse>> getTodayTasks() {
        return ResponseEntity.ok(careTaskService.getTodayTasks());
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ToggleCareTaskResponse> toggleTask(
            @PathVariable("id") String id,
            @RequestBody(required = false) ToggleCareTaskRequest request
    ) {
        return ResponseEntity.ok(careTaskService.toggleTask(id, request));
    }
}
