package com.cuidapapai.api.controller;

import com.cuidapapai.api.config.UserResolver;
import com.cuidapapai.api.dto.CareTaskResponse;
import com.cuidapapai.api.dto.CreateCareTaskRequest;
import com.cuidapapai.api.dto.ToggleCareTaskRequest;
import com.cuidapapai.api.dto.ToggleCareTaskResponse;
import com.cuidapapai.api.service.CareTaskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/care-tasks")
@RequiredArgsConstructor
public class CareTaskController {

    private final CareTaskService careTaskService;
    private final UserResolver userResolver;

    @GetMapping("/today")
    public ResponseEntity<List<CareTaskResponse>> getTodayTasks(HttpServletRequest request) {
        String userId = userResolver.resolveUserId(request);
        return ResponseEntity.ok(careTaskService.getTodayTasks(userId));
    }

    @PostMapping
    public ResponseEntity<CareTaskResponse> createCareTask(
            HttpServletRequest request,
            @RequestBody CreateCareTaskRequest body
    ) {
        String userId = userResolver.resolveUserId(request);
        CareTaskResponse created = careTaskService.createCareTask(userId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ToggleCareTaskResponse> toggleTask(
            @PathVariable("id") String id,
            @RequestBody(required = false) ToggleCareTaskRequest request
    ) {
        return ResponseEntity.ok(careTaskService.toggleTask(id, request));
    }
}
