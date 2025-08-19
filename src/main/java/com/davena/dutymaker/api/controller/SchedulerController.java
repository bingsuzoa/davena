package com.davena.dutymaker.api.controller;

import com.davena.dutymaker.api.dto.schedule.RequirementRuleRequest;
import com.davena.dutymaker.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Pattern;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.YearMonth;

@Controller
@RequiredArgsConstructor
public class SchedulerController {

    private ScheduleService scheduleService;

    @PutMapping("/requriements")
    public void updateRequirement(@PathVariable Long wardId, RequirementRuleRequest rules) {
        scheduleService.updateRequirementRule(wardId, rules);
    }

    @PostMapping("/{wardId}/schedules/{yearMonth}")
    public void getNetSchedules(@PathVariable Long wardId,
                                @PathVariable @Pattern(regexp = "\\d{4}-\\d{2}") String yearMonth) {
        scheduleService.getScheduleView(wardId, YearMonth.parse(yearMonth));
    }
}
