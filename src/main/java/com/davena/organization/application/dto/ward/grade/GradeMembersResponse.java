package com.davena.organization.application.dto.ward.grade;

import java.util.Map;
import java.util.UUID;

public record GradeMembersResponse(
        UUID wardId,
        UUID supervisorId,
        Map<UUID, GradeDto> gradeMembers
) {
}
