package com.davena.dutymaker.domain.service;

import com.davena.dutymaker.application.dto.ScheduleDto;
import com.davena.dutymaker.domain.model.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleUpdateService {

    private final ScheduleReadService scheduleReadService;

    public ScheduleDto finalizedSchedule(UUID scheduleId) {
        Schedule schedule = scheduleReadService.getScheduleById(scheduleId);
        schedule.finalizeStatus();
        return scheduleReadService.getScheduleDto(schedule);
    }
}
