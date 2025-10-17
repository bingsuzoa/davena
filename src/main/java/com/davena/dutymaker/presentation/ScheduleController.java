package com.davena.dutymaker.presentation;

import com.davena.dutymaker.application.dto.ScheduleDto;
import com.davena.dutymaker.application.dto.request.CreateScheduleRequest;
import com.davena.dutymaker.application.dto.request.GetScheduleRequest;
import com.davena.dutymaker.application.dto.request.UpdateCellRequest;
import com.davena.dutymaker.domain.service.ScheduleCreateService;
import com.davena.dutymaker.domain.service.ScheduleReadService;
import com.davena.dutymaker.domain.service.ScheduleUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleCreateService scheduleCreateService;
    private final ScheduleReadService scheduleReadService;
    private final ScheduleUpdateService scheduleUpdateService;

    @PostMapping("/new")
    public ScheduleDto createNewSchedule(@RequestBody CreateScheduleRequest request) {
        return scheduleCreateService.createNewSchedule(request);
    }

    @GetMapping
    public ScheduleDto getScheduleByWard(@RequestBody GetScheduleRequest request) {
        return scheduleReadService.getScheduleDto(request);
    }

    @PostMapping("/custom")
    public ScheduleDto saveCustomSchedule(@RequestBody UpdateCellRequest request) {
        return scheduleCreateService.saveCustomSchedule(request);
    }

    @PutMapping("/{scheduleId}/finalize")
    public ScheduleDto finalizeSchedule(@PathVariable UUID scheduleId) {
        return scheduleUpdateService.finalizedSchedule(scheduleId);
    }

//    @PostMapping("/{scheduleId}/generate")
//    public ScheduleDto runAutoScheduling(@PathVariable UUID scheduleId) {
//
//    }
}
