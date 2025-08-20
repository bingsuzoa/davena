package com.davena.dutymaker.api.controller;

import com.davena.dutymaker.api.dto.schedule.RequirementRuleRequest;
import com.davena.dutymaker.api.dto.schedule.backfill.BackfillGrid;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.service.BackfillService;
import com.davena.dutymaker.service.DraftService;
import com.davena.dutymaker.service.ScheduleService;
import com.davena.dutymaker.service.generator.PreCheck;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Pattern;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Controller
@RequiredArgsConstructor
public class SchedulerController {

    private ScheduleService scheduleService;
    private DraftService draftService;
    private BackfillService backfillService;
    private PreCheck preCheck;

    @PutMapping("/requriements")
    public void updateRequirement(@PathVariable Long wardId, RequirementRuleRequest rules) {
        scheduleService.updateRequirementRule(wardId, rules);
    }

    @PostMapping("/{wardId}/schedules/{yearMonth}")
    public void getNetSchedules(@PathVariable Long wardId,
                                @PathVariable @Pattern(regexp = "\\d{4}-\\d{2}") String yearMonth) {
        scheduleService.getScheduleView(wardId, YearMonth.parse(yearMonth));
    }

    @PostMapping("/{scheduleId}/draft")
    public void updateDraft(@PathVariable Long scheduleId, @RequestBody DraftPayload payload) {
        draftService.updateDraft(scheduleId, payload);
    }

    @GetMapping("/{wardId}/backfill-grid/{targetYm}")
    public void getEmptyBackfillGrid(@PathVariable Long wardId, @PathVariable String ym) {
        YearMonth target = YearMonth.parse(ym);
        backfillService.buildEmptyBackfillGrid(wardId, target);
    }

    @PostMapping("/{scheduleId}/backfill-grid/{targetYm}")
    public void updateBakfillGrid(@PathVariable Long scheduleId, @RequestBody DraftPayload payload) {
        backfillService.updateDraft(scheduleId, payload);
    }

    @PostMapping("/wards/{wardId}/schedules/{scheduleId}/preCheck")
    public void preCheck(@PathVariable Long wardId, @PathVariable Long scheduleId) {
        preCheck.preCheckWard(wardId, scheduleId);
    }
}
