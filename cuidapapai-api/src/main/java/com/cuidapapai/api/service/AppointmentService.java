package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.AppointmentResponse;
import com.cuidapapai.api.dto.CreateAppointmentRequest;
import com.cuidapapai.api.model.Appointment;
import com.cuidapapai.api.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public List<AppointmentResponse> getUpcomingAppointments(String userId, String startDateStr, String endDateStr) {
        List<Appointment> appointments;

        if (startDateStr != null && endDateStr != null) {
            try {
                LocalDate start = LocalDate.parse(startDateStr);
                LocalDate end = LocalDate.parse(endDateStr);
                OffsetDateTime startDateTime = start.atStartOfDay().atOffset(ZoneOffset.UTC);
                OffsetDateTime endDateTime = end.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
                appointments = appointmentRepository.findByUserIdAndEventDatetimeBetweenOrderByEventDatetimeAsc(userId, startDateTime, endDateTime);
            } catch (Exception e) {
                appointments = appointmentRepository.findByUserIdOrderByEventDatetimeAsc(userId);
            }
        } else {
            appointments = appointmentRepository.findByUserIdOrderByEventDatetimeAsc(userId);
        }

        return appointments.stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentResponse createAppointment(String userId, CreateAppointmentRequest request) {
        if (request == null || request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O título do compromisso é obrigatório.");
        }

        OffsetDateTime eventDateTime = null;
        if (request.getEventDatetime() != null) {
            eventDateTime = request.getEventDatetime();
        } else if (request.getDate() != null && !request.getDate().isBlank()) {
            try {
                eventDateTime = OffsetDateTime.parse(request.getDate());
            } catch (Exception e) {
                try {
                    LocalDate localDate = LocalDate.parse(request.getDate());
                    eventDateTime = localDate.atTime(10, 0).atOffset(ZoneOffset.UTC);
                } catch (Exception ex) {
                    eventDateTime = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);
                }
            }
        } else {
            eventDateTime = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);
        }

        String aptId = "apt_" + UUID.randomUUID().toString().substring(0, 8);
        String color = request.getColorHex() != null && !request.getColorHex().isBlank()
                ? request.getColorHex()
                : "#000080";

        Appointment appointment = Appointment.builder()
                .id(aptId)
                .userId(userId)
                .title(request.getTitle().trim())
                .subtitle(request.getSubtitle())
                .eventDatetime(eventDateTime)
                .colorHex(color)
                .build();

        appointmentRepository.save(appointment);

        return toResponse(appointment);
    }

    public AppointmentResponse updateAppointment(String userId, String id, CreateAppointmentRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os dados do compromisso são obrigatórios.");
        }

        Appointment appointment = getUserAppointment(userId, id);

        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O título do compromisso é obrigatório.");
            }
            appointment.setTitle(request.getTitle().trim());
        }

        if (request.getSubtitle() != null) {
            appointment.setSubtitle(request.getSubtitle());
        }

        if (request.getEventDatetime() != null) {
            appointment.setEventDatetime(request.getEventDatetime());
        } else if (request.getDate() != null && !request.getDate().isBlank()) {
            try {
                appointment.setEventDatetime(OffsetDateTime.parse(request.getDate()));
            } catch (Exception e) {
                try {
                    LocalDate localDate = LocalDate.parse(request.getDate());
                    appointment.setEventDatetime(localDate.atTime(10, 0).atOffset(ZoneOffset.UTC));
                } catch (Exception ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data do compromisso é inválida.");
                }
            }
        }

        if (request.getColorHex() != null && !request.getColorHex().isBlank()) {
            appointment.setColorHex(request.getColorHex());
        }

        return toResponse(appointmentRepository.save(appointment));
    }

    public void deleteAppointment(String userId, String id) {
        appointmentRepository.delete(getUserAppointment(userId, id));
    }

    private Appointment getUserAppointment(String userId, String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compromisso não encontrado com id: " + id));
        if (!userId.equals(appointment.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compromisso não encontrado com id: " + id);
        }
        return appointment;
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
