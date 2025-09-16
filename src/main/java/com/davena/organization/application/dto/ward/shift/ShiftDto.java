package com.davena.organization.application.dto.ward.shift;

import com.davena.organization.domain.model.ward.DayType;

import java.time.LocalTime;
import java.util.UUID;

public record ShiftDto(
        UUID id,
        DayType dayType,
        String name,
        int startHour,
        int startMinute,
        int endHour,
        int endMinute
) {
}
