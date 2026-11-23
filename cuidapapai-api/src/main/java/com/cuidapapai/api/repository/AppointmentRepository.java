package com.cuidapapai.api.repository;

import com.cuidapapai.api.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    List<Appointment> findByEventDatetimeBetweenOrderByEventDatetimeAsc(OffsetDateTime start, OffsetDateTime end);
    List<Appointment> findAllByOrderByEventDatetimeAsc();
}
