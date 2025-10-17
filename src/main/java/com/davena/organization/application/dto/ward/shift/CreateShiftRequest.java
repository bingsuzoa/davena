package com.davena.organization.application.dto.ward.shift;

import com.davena.organization.domain.model.ward.DayType;

import java.util.UUID;

public record CreateShiftRequest(
        UUID wardId,
        UUID supervisorId,
        DayType dayType,
        String shiftName,
        int startHour,
        int startMinute,
        int endHour,
        int endMinute
) {
}
