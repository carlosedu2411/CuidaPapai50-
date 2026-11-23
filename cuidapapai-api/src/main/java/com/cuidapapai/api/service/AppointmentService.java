package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.AppointmentResponse;
import com.cuidapapai.api.model.Appointment;
import com.cuidapapai.api.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public List<AppointmentResponse> getUpcomingAppointments(String startDateStr, String endDateStr) {
        List<Appointment> appointments;

        if (startDateStr != null && endDateStr != null) {
            try {
                LocalDate start = LocalDate.parse(startDateStr);
                LocalDate end = LocalDate.parse(endDateStr);
                OffsetDateTime startDateTime = start.atStartOfDay().atOffset(ZoneOffset.UTC);
                OffsetDateTime endDateTime = end.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
                appointments = appointmentRepository.findByEventDatetimeBetweenOrderByEventDatetimeAsc(startDateTime, endDateTime);
            } catch (Exception e) {
                appointments = appointmentRepository.findAllByOrderByEventDatetimeAsc();
            }
        } else {
            appointments = appointmentRepository.findAllByOrderByEventDatetimeAsc();
        }

        return appointments.stream()
                .map(this::toResponse)
                .toList();
    }

    private AppointmentResponse toResponse(Appointment apt) {
        OffsetDateTime dt = apt.getEventDatetime();
        String dayShort = getDayOfWeekShort(dt.getDayOfWeek());
        String dayNumber = String.valueOf(dt.getDayOfMonth());
        String timeStr = dt.format(DateTimeFormatter.ofPattern("HH:mm"));

        return AppointmentResponse.builder()
                .id(apt.getId())
                .date(dt.toString())
                .dayOfWeekShort(dayShort)
                .dayNumber(dayNumber)
                .time(timeStr)
                .title(apt.getTitle())
                .subtitle(apt.getSubtitle())
                .colorHex(apt.getColorHex())
                .build();
    }

    private String getDayOfWeekShort(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "SEG";
            case TUESDAY -> "TER";
            case WEDNESDAY -> "QUA";
            case THURSDAY -> "QUI";
            case FRIDAY -> "SEX";
            case SATURDAY -> "SÁB";
            case SUNDAY -> "DOM";
        };
    }
}
