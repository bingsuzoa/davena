package com.davena.organization.domain.model.user;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User {

    public User(
            UUID id,
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

    public static final String NOT_EXIST_REQUEST_ENROLLMENT = "병동 가입 신청부터 진행해주세요.";

    private UUID id;
    private String name;
    private String loginId;
    private String password;
    private String phoneNumber;

    private WardEnrollment wardEnrollment = new WardEnrollment(null, JoinStatus.NONE);


    public static User create(String name, String loginId, String password, String phoneNumber) {
        return new User(UUID.randomUUID(), name, loginId, password, phoneNumber);
    }

    public void applyForWard(UUID wardId) {
        this.wardEnrollment = new WardEnrollment(wardId, JoinStatus.PENDING);
    }

    public void approveEnrollment(UUID wardId) {
        if (wardEnrollment == null) {
            throw new IllegalArgumentException(NOT_EXIST_REQUEST_ENROLLMENT);
        }
        wardEnrollment = new WardEnrollment(wardId, JoinStatus.APPROVE);
    }

    public void rejectEnrollment(UUID wardId) {
        wardEnrollment = new WardEnrollment(null, JoinStatus.NONE);
    }

    public JoinStatus getStatus() {
        return wardEnrollment.status();
    }

    public UUID getWardId() {
        return wardEnrollment.wardId();
    }
}
