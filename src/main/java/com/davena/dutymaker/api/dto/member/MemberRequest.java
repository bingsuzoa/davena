package com.davena.dutymaker.api.dto.member;

public record MemberRequest(
        String name,
        String nickName,
        String phoneNumber,
        String password
) {
}
