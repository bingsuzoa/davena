package com.davena.dutymaker.api.dto.schedule.requirement;

import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;

import java.util.Map;

public record RequirementRequest(
        Long teamId,
        Map<DayType, Map<ShiftType, Integer>> requirementBox
) {
}
