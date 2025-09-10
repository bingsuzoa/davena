package com.davena.organization.application.dto.ward.shift;

import com.davena.organization.domain.model.ward.DayType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record WardShiftsResponse(
        UUID wardId,
        UUID supervisorId,
        Map<DayType, List<ShiftDto>> shifts
) {
}
