package com.davena.dutymaker.domain.organization.team;

import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import lombok.Getter;

import java.util.*;

@Getter
public class TeamState {

    public TeamState(
            Map<ShiftType, Integer> remain,
            List<ShiftType> shiftTypes,
            PriorityQueue<Member> chargeOrder
    ) {
        this.remain = remain;
        this.shiftTypes = shiftTypes;
        this.chargeOrder = chargeOrder;
    }

    Map<ShiftType, Integer> remain;
    List<ShiftType> shiftTypes;
    Map<SkillGrade, Integer> appliedGrade = new HashMap<>();
    Map<ShiftType, Boolean> chargeAssigned = new HashMap<>();
    PriorityQueue<Member> chargeOrder;

    public boolean needChargeFor(ShiftType shiftType) {
        return !chargeAssigned.getOrDefault(shiftType, false);
    }

    public void apply(ShiftType shiftType, SkillGrade skillGrade, boolean isCharge) {
        Integer currentRemain = remain.getOrDefault(shiftType, 0);

        if(currentRemain <= 0) {
            return;
        }
        remain.put(shiftType, currentRemain - 1);
        appliedGrade.merge(skillGrade, 1, Integer::sum);

        if (isCharge) {
            chargeAssigned.put(shiftType, true);
        }
    }

}
