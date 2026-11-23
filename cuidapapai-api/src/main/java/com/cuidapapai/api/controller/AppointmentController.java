package com.cuidapapai.api.controller;

import com.cuidapapai.api.dto.AppointmentResponse;
import com.cuidapapai.api.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments(
            @RequestParam(name = "start_date", required = false) String startDate,
            @RequestParam(name = "end_date", required = false) String endDate
    ) {
        return ResponseEntity.ok(appointmentService.getUpcomingAppointments(startDate, endDate));
    }
}
