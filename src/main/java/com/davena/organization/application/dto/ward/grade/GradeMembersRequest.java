package com.davena.organization.application.dto.ward.grade;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GradeMembersRequest(
        UUID supervisorId,
        UUID wardId,
        Map<UUID, List<UUID>> usersOfGrade
) {
}
