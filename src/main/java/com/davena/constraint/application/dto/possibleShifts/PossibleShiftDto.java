package com.davena.constraint.application.dto.possibleShifts;

import com.davena.organization.domain.model.ward.DayType;

import java.util.UUID;

public record PossibleShiftDto(
        DayType dayType,
        UUID shiftId,
        String shiftName,
        boolean isPossible
) {
}
