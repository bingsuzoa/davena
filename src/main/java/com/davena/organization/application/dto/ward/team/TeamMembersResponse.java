package com.davena.organization.application.dto.ward.team;

import com.davena.organization.application.dto.user.UserDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TeamMembersDto(
        UUID supervisorId,
        UUID wardId,
        Map<TeamDto, List<UserDto>> usersOfTeam
) {
}
