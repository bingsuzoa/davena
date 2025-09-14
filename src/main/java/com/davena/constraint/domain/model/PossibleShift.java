package com.davena.constraint.domain.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PossibleShift {

    public PossibleShift(
            UUID shiftId,
            String name
    ) {
        this.shiftId = shiftId;
        this.name = name;
    }

    private UUID shiftId;
    private String name;
    private boolean isPossible = true;

    public void updatePossibleShift(boolean isPossible) {
        this.isPossible = isPossible;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
