package com.davena.dutymaker.domain.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ShiftState {

    public ShiftState(UUID shiftId, int initCount) {
        this.shiftId = shiftId;
        this.remainCount = initCount;
        haveCharge = false;
    }

    private UUID shiftId;
    private int remainCount;
    private boolean haveCharge;

    public void haveCharge() {
        haveCharge = true;
    }

    public void decrementRemainCount() {
        if(remainCount > 0) {
            remainCount--;
        }
    }
}
