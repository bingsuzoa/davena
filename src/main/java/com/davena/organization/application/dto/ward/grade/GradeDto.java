package com.davena.organization.application.dto.ward.grade;

import com.davena.organization.application.dto.ward.MemberDto;

import java.util.List;
import java.util.UUID;

public record GradeDto(
        UUID id,
        String name,
        boolean isDefault,
        List<MemberDto> membersOfGrade
) {
}
