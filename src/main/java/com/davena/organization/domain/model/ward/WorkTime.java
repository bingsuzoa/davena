package com.davena.organization.domain.model.ward;

import java.time.LocalTime;

public record WorkTime(
        LocalTime startTime,
        LocalTime endTime
) {
}
