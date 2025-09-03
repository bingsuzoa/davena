package com.davena.organization.application.dto.user;

import com.davena.organization.domain.model.user.User;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String name
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId().id(), user.getName());
    }
}
