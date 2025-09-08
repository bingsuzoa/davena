package com.davena.organization.application.dto.ward.grade;

import java.util.UUID;

public record GradeRequest(
        UUID supervisorId,
        UUID wardId,
        String name
) {
}
