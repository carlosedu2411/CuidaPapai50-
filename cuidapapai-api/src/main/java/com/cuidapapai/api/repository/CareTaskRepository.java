package com.cuidapapai.api.repository;

import com.cuidapapai.api.model.CareTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CareTaskRepository extends JpaRepository<CareTask, String> {
    List<CareTask> findByTaskDateOrderByIdAsc(LocalDate taskDate);
    List<CareTask> findByUserIdAndTaskDateOrderByIdAsc(String userId, LocalDate taskDate);
}
