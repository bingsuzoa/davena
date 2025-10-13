package com.davena.dutymaker.application.dto;

import java.util.UUID;

public record AssignmentDto(
        UUID cellId,
        String memberName,
        String shiftName
) {
}