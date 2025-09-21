package com.davena.organization.application.dto.ward.team;

import com.davena.organization.application.dto.ward.MemberDto;

import java.util.List;
import java.util.UUID;

public record TeamDto(
        UUID id,
        String name,
        boolean isDefault,
        List<MemberDto> members
) {
}
