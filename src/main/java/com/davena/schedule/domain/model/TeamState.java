package com.davena.schedule.domain.model;

import com.davena.constraint.domain.model.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class TeamState {

    public TeamState(
            UUID teamId,
            List<UUID> chargeOrder,
            Map<UUID, ShiftState> shiftStates
    ) {
        this.teamId = teamId;
        this.chargeOrder = chargeOrder;
        this.shiftStates = shiftStates;
    }

    private UUID teamId;
    private List<UUID> chargeOrder;
    private Map<UUID, ShiftState> shiftStates;

    public boolean canAssign(UUID shiftId, Member member) {
        UUID priorityGrade = getPriorityGrade(shiftStates.get(shiftId));
        return member.getGradeId().equals(priorityGrade);
    }

    private UUID getPriorityGrade(ShiftState shiftState) {
        return shiftState.getAppliedGrade().entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("No grade found"));
    }

    public TeamState copy() {
        Map<UUID, ShiftState> copiedShiftStates = this.shiftStates.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().copy()
                ));

        return new TeamState(
                this.teamId,
                new ArrayList<>(this.chargeOrder), // List 복사
                copiedShiftStates
        );
    }
}

