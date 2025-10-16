package com.davena.dutymaker.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamState {

    public TeamState(UUID teamId) {
        this.teamId = teamId;
    }

    UUID teamId;
    List<UUID> chargeMemberRanks;
    List<ShiftState> shiftStates;

    public void initChargeMemberRanks(List<UUID> ranks) {
        chargeMemberRanks = chargeMemberRanks;
    }

    public void initShiftStates(List<ShiftState> states) {
        shiftStates = states;
    }
}
