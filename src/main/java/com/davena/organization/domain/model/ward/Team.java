package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Team {

    private Team(
            TeamId id,
            WardId wardId,
            String name,
            boolean isDefault
    ) {
        this.id = id;
        this.wardId = wardId;
        this.name = name;
        this.isDefault = isDefault;
    }

    private TeamId id;
    private String name;
    private WardId wardId;
    private boolean isDefault;


    protected static Team createDefaultTeam(String name, WardId wardId) {
        return new Team(new TeamId(UUID.randomUUID()), wardId, name, true);
    }

    protected static Team createTeam(String name, WardId wardId) {
        return new Team(new TeamId(UUID.randomUUID()), wardId, name, false);
    }

}
