package com.davena.dutymaker.api.controller;

import com.davena.dutymaker.api.dto.schedule.RequirementRuleRequest;
import com.davena.dutymaker.service.ScheduleRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@RequiredArgsConstructor
public class SchedulerController {

    private ScheduleRuleService scheduleRuleService;

    @PutMapping("/requriements")
    public void updateRequirement(@PathVariable Long wardId, RequirementRuleRequest rules) {
        scheduleRuleService.updateRequirementRule(wardId, rules);
    }
}
