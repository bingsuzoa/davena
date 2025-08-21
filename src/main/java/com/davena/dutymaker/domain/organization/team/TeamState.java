package com.davena.dutymaker.domain.organization.team;

import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class TeamState {

    public TeamState(
            int workDate,
            Team team,
            Map<ShiftType, Integer> remain,
            PriorityQueue<Member> chargeOrder
    ) {
        this.workDate = workDate;
        this.team = team;
        this.remain = remain;
        this.chargeOrder = chargeOrder;
    }

    int workDate;
    Team team;
    Map<ShiftType, Integer> remain;
    Map<ShiftType, Integer> appliedGrade = new HashMap<>();
    Map<ShiftType, Boolean> chargeAssigned = new HashMap<>();
    PriorityQueue<Member> chargeOrder;

    public boolean needChargeFor(ShiftType shiftType) {
        return !chargeAssigned.getOrDefault(shiftType, false);
    }

    public void apply(Member member, ShiftType shiftType, SkillGrade skillGrade, boolean isCharge) {
        remain.computeIfPresent(shiftType, (k, v) -> v - 1);
        appliedGrade.computeIfPresent(shiftType, (k, v) -> v + 1);

        if (isCharge) {
            chargeAssigned.put(shiftType, true);
        }
    }

}
