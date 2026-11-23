package com.cuidapapai.api.repository;

import com.cuidapapai.api.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, String> {
    List<Medication> findByScheduledDateOrderByScheduledTimeAsc(LocalDate scheduledDate);
    List<Medication> findByUserIdAndScheduledDateOrderByScheduledTimeAsc(String userId, LocalDate scheduledDate);
}
