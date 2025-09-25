package com.davena.schedule.domain.port;

import com.davena.schedule.domain.model.Schedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository {

    Optional<Schedule> getScheduleByWardIdAndYearAndMonth(UUID wardId, int year, int month);
}
