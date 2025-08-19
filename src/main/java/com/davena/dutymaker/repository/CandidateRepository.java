package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository {

    int countByScheduleId(Long scheduleId);
}
