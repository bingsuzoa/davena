package com.davena.constraint.application.dto.shiftRequest;

import java.util.UUID;

public record MemberDto(
        UUID memberId,
        String name
) {
}
