package com.davena.dutymaker.api.dto.schedule;

import com.davena.dutymaker.domain.policy.DayType;

public record RequirementRuleRequest(
        Long teamId,
        DayType dayType,
        Long shiftTypeId,
        int required
) {
}
