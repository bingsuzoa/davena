package com.davena.organization.domain.model.team;

import com.davena.organization.domain.model.ward.WardId;
import lombok.Getter;

@Getter
public class Team {

    public Team(
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
}
