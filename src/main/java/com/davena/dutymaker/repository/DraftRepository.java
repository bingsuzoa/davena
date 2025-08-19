package com.davena.dutymaker.repository;

import com.davena.dutymaker.api.dto.schedule.payload.draft.Draft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DraftRepository extends JpaRepository<Draft, Long> {
    Optional<Draft> findByScheduleId(Long scheduleId);
}
