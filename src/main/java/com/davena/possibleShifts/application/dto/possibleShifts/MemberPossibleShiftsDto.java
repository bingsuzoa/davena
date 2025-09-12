package com.davena.possibleShifts.application.dto.possibleShifts;

import com.davena.organization.domain.model.ward.DayType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record MemberPossibleShiftsDto(
        UUID userId,
        String userName,
        Map<DayType, List<PossibleShiftDto>> memberPossibleShifts
) {
}
