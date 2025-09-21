package com.davena.organization.application.dto.user;

import com.davena.organization.domain.model.user.User;

import java.util.UUID;

public record UserDto(
        UUID id,
        String name
) {
    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getName());
    }
}
