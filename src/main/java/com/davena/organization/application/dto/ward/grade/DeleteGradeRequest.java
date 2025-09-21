package com.davena.organization.application.dto.ward.grade;

import java.util.UUID;

public record DeleteGradeRequest(
        UUID wardId,
        UUID supervisorId,
        UUID gradeId
) {
}
