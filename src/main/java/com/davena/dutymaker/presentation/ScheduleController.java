package com.davena.dutymaker.presentation;

import com.davena.dutymaker.application.dto.request.CreateScheduleRequest;
import com.davena.dutymaker.application.dto.ScheduleDto;
import com.davena.dutymaker.application.dto.request.FinalizedScheduleRequest;
import com.davena.dutymaker.application.dto.request.GetScheduleRequest;
import com.davena.dutymaker.domain.service.ScheduleReadService;
import com.davena.dutymaker.domain.service.ScheduleCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleCreateService scheduleCreateService;
    private final ScheduleReadService scheduleReadService;

    @PostMapping("/new")
    public ScheduleDto createNewSchedule(@RequestBody CreateScheduleRequest request) {
        return scheduleCreateService.createNewSchedule(request);
    }

    @GetMapping
    public ScheduleDto getScheduleByWard(@RequestBody GetScheduleRequest request) {
        return scheduleReadService.getScheduleDto(request);
    }

    @PostMapping("/custom")
    public ScheduleDto saveCustomSchedule(@RequestBody FinalizedScheduleRequest request) {
        return scheduleCreateService.saveCustomSchedule(request);
    }
}
