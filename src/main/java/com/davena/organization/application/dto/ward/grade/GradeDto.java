package com.davena.organization.application.dto.ward.grade;

import java.util.UUID;

public record GradeDto(
        UUID id,
        String name,
        boolean isDefault
) {
}
