package com.davena.schedule.domain.service;

import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.organization.domain.model.ward.*;
import com.davena.schedule.application.dto.GenerateRequest;
import com.davena.schedule.domain.model.ShiftState;
import com.davena.schedule.domain.model.TeamState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamStateService {

    private final WardService wardService;
    private final MemberService memberService;

    public List<TeamState> initTeamStates(GenerateRequest request, int day) {
        Ward ward = wardService.getWard(request.wardId());

        return ward.getTeams().stream()
                .map(team -> new TeamState(
                        team.getId(),
                        memberService.getChargeMemberOfWard(ward, team),
                        initShiftStates(ward, team, request, day)
                ))
                .toList();
    }

    private Map<UUID, ShiftState> initShiftStates(Ward ward, Team team, GenerateRequest request, int day) {
        DayType dayType = DayType.getDayType(request.year(), request.month(), day);
        Map<UUID, Integer> shiftRequirements = ward.getRequirements().get(team.getId());

        Map<UUID, ShiftState> shiftStates = new HashMap<>();
        for (Map.Entry<UUID, Integer> entry : shiftRequirements.entrySet()) {
            Shift shift = ward.getShift(entry.getKey());
            int remain = shift.getDayType().equals(dayType) ? entry.getValue() : 0;

            shiftStates.put(entry.getKey(),
                    new ShiftState(shift.getId(), remain, false, initGradeMap(ward)));
        }
        return shiftStates;
    }

    private Map<UUID, Integer> initGradeMap(Ward ward) {
        return ward.getGrades().stream()
                .collect(Collectors.toMap(Grade::getId, g -> 0));
    }
}
