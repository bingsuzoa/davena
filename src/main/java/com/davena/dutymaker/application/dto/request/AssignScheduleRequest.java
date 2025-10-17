package com.davena.dutymaker.application.dto.request;

import java.util.UUID;

public record AssignScheduleRequest(
        UUID wardId,
        UUID scheduleId,
        int year,
        int month
) {
}
