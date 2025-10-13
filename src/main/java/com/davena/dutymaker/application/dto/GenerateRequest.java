package com.davena.dutymaker.application.dto;

import java.util.UUID;

public record GenerateRequest(
        UUID wardId,
        UUID scheduleId,
        int year,
        int month
) {
}
