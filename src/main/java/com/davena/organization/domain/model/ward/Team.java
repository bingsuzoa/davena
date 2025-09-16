package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class Team {

    protected Team(
            UUID id,
            UUID wardId,
            String name,
            boolean isDefault
    ) {
        this.id = id;
        this.wardId = wardId;
        this.name = name;
        this.isDefault = isDefault;
    }

    private UUID id;
    private String name;
    private UUID wardId;
    private boolean isDefault;

    public static final String DEFAULT_TEAM = "A팀";
    public static final String CAN_NOT_REMOVE_DEFAULT_TEAM = "기본 팀은 삭제가 불가능합니다.";

    protected void validateDefaultTeam() {
        if (isDefault) {
            throw new IllegalArgumentException(CAN_NOT_REMOVE_DEFAULT_TEAM);
        }
    }
}
