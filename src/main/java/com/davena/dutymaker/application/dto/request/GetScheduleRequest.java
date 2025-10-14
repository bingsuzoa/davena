package com.davena.dutymaker.application.dto.request;

import java.util.UUID;

public record GetScheduleRequest(
        UUID wardId,
        UUID scheduleId,
        int year,
        int month
) {
}
