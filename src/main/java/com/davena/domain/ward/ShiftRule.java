package com.davena.domain.ward;

import java.util.Map;

public record ShiftRule(
        DayType dayType,
        Map<Long, Integer> requiredByShift
) {
}
