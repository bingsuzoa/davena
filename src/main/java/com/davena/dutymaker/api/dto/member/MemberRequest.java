package com.davena.dutymaker.api.dto.member;

public record MemberRequest(
        String nickName,
        String password,
        String name,
        String phoneNumber
) {
}
