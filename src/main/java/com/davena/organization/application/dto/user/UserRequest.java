package com.davena.organization.application.dto.user;

public record UserRequest(
        String name,
        String loginId,
        String password,
        String phoneNumber
) {
}
