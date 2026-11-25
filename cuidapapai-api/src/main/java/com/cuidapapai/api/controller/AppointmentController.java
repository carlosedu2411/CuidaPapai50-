package com.cuidapapai.api.controller;

import com.cuidapapai.api.config.UserResolver;
import com.cuidapapai.api.dto.AppointmentResponse;
import com.cuidapapai.api.dto.CreateAppointmentRequest;
import com.cuidapapai.api.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserResolver userResolver;

    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments(
            HttpServletRequest request,
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate
    ) {
        String userId = userResolver.resolveUserId(request);
        return ResponseEntity.ok(appointmentService.getUpcomingAppointments(userId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            HttpServletRequest request,
            @RequestBody CreateAppointmentRequest body
    ) {
        String userId = userResolver.resolveUserId(request);
        AppointmentResponse created = appointmentService.createAppointment(userId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
