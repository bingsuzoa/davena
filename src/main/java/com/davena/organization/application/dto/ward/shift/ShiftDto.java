package com.davena.organization.application.dto.ward.shift;

import java.util.UUID;

public record ShiftDto(
        UUID shiftId,
        String name,
        boolean isDefault
) {
}
