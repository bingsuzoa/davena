package com.davena.organization.domain.model.user;

import lombok.Getter;

@Getter
public class User {

    private UserId id;
    private String name;
    private String loginId;
    private String password;
    private String phoneNumber;
}
