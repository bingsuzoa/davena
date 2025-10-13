package com.davena.dutymaker.domain.port;

import com.davena.dutymaker.domain.model.schedule.Schedule;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository {
    Optional<Schedule> getScheduleByWardIdAndYearAndMonth(UUID wardId, int year, int month);
}
