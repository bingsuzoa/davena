package com.davena.dutymaker.presentation;

import com.davena.dutymaker.application.dto.ScheduleResponse;
import com.davena.dutymaker.application.dto.GetScheduleRequest;
import com.davena.dutymaker.domain.service.ScheduleReadService;
import com.davena.dutymaker.domain.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleReadService scheduleReadService;

    @PostMapping("/new")
    public ScheduleResponse createSchedule(@RequestBody GetScheduleRequest request) {
        return scheduleReadService.createSchedule(request);
    }

    @GetMapping
    public ScheduleResponse getScheduleByWard(@RequestBody GetScheduleRequest request) {
        return scheduleReadService.getSchedule(request);
    }
}
