package com.davena.organization.application.dto.ward.grade;

import java.util.UUID;

public record GetGradeRequest(
        UUID wardId,
        UUID supervisorId
) {
}
