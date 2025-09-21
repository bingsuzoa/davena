package com.davena.organization.application.dto.ward.shift;

import com.davena.organization.domain.model.ward.DayType;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalTime;
import java.util.UUID;

public record ShiftDto(
        UUID id,
        DayType dayType,
        String name,
        boolean isOff,
        Integer startHour,
        Integer startMinute,
        Integer endHour,
        Integer endMinute
) {
}
