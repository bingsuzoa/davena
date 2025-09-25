package com.davena.schedule.domain.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ShiftState {

    public ShiftState(
            UUID id,
            int remain,
            boolean hasCharge,
            Map<UUID, Integer> appliedGrade
    ) {
        this.id = id;
        this.remain = remain;
        this.hasCharge = hasCharge;
        this.appliedGrade = appliedGrade;
    }

    UUID id;
    int remain;
    boolean hasCharge;
    Map<UUID, Integer> appliedGrade;

    public void updateRemain() {
        if (remain > 0) {
            remain--;
        }
    }

    public void setHasCharge(boolean hasCharge) {
        this.hasCharge = hasCharge;
    }

    public ShiftState copy() {
        return new ShiftState(this.id, this.remain, this.hasCharge, new HashMap<>(this.appliedGrade));
    }
}
