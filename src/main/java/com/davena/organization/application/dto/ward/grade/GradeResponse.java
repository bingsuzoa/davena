package com.davena.organization.application.dto.ward.grade;

import java.util.UUID;

public record GradeResponse(
        UUID wardId,
        UUID gradeId,
        String name
) {
}
