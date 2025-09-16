package com.davena.organization.application.dto.ward;

import com.davena.constraint.domain.model.Member;

import java.util.UUID;

public record MemberDto(
        UUID id,
        String name
) {
    public static MemberDto from(Member member) {
        return new MemberDto(member.getUserId(), member.getName());
    }
}
