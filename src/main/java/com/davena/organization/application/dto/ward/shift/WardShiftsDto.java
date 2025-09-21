package com.davena.organization.application.dto.ward.shift;

import java.util.List;
import java.util.UUID;

public record WardShiftsDto(
        UUID wardId,
        UUID supervisorId,
        List<ShiftDto> shifts
) {
}
