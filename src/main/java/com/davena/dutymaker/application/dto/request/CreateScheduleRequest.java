package com.davena.dutymaker.application.dto.request;

import java.util.UUID;

public record CreateScheduleRequest(
        UUID wardId,
        int year,
        int month
) {
}
