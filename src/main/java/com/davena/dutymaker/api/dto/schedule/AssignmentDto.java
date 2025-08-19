package com.davena.dutymaker.api.dto.schedule;

import java.time.LocalDate;

public record AssignmentDto(
        LocalDate assignDate,
        Long memberId,
        String memberName,
        String shift
) {
}
