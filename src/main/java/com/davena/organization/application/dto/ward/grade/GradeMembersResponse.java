package com.davena.organization.application.dto.ward.grade;

import com.davena.organization.application.dto.user.UserDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GradeMembersResponse(
        UUID supervisorId,
        UUID wardId,
        Map<GradeDto, List<UserDto>> usersOfGrade
) {
}
