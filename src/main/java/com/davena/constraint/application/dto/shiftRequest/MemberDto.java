package com.davena.constraint.application.dto.shiftRequest;

import com.davena.constraint.domain.model.Member;

import java.util.UUID;

public record MemberDto(
        UUID memberId,
        String name
) {
}
