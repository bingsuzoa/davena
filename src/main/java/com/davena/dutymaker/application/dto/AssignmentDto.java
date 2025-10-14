package com.davena.dutymaker.application.dto;

import java.util.UUID;

public record AssignmentDto(
        UUID cellId,
        UUID memberId,
        UUID shiftId,
        String memberName,
        String shiftName
) {
}