package com.davena.organization.application.dto.ward.shift;

import java.time.LocalTime;
import java.util.UUID;

public record ShiftDto(
        UUID id,
        String name,
        LocalTime statTime,
        LocalTime endTime
) {
}
