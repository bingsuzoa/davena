package com.davena.dutymaker.application.dto;

import java.util.List;
import java.util.UUID;

public record ScheduleResponse(
        UUID scheduleId,
        int year,
        int month,
        int lastDate,
        List<CandidateDto> candidates
) {
}