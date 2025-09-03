package com.davena.organization.domain.model.user;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User {

    public User(
            UserId id,
            String name,
            String loginId,
            String password,
            String phoneNumber
    ) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    private UserId id;
    private String name;
    private String loginId;
    private String password;
    private String phoneNumber;

    public static User create(String name, String loginId, String password, String phoneNumber) {
        return new User(new UserId(UUID.randomUUID()), name, loginId, password, phoneNumber);
    }
}
