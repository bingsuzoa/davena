package com.davena.dutymaker.domain.service.state;

import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.constraint.domain.model.Member;
import com.davena.dutymaker.domain.model.DayState;
import com.davena.dutymaker.domain.model.DayTypeResolver;
import com.davena.dutymaker.domain.model.ShiftState;
import com.davena.dutymaker.domain.model.TeamState;
import com.davena.organization.domain.model.ward.DayType;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Team;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DayStateService {

    private final WardService wardService;
    private final MemberService memberService;

    public DayState initDayState(UUID wardId, int year, int month, int day) {
        Ward ward = wardService.getWard(wardId);

        List<TeamState> teamStates = new ArrayList<>();
        for (Team team : ward.getTeams()) {
            TeamState teamState = new TeamState(team.getId());
            teamState.initChargeMemberRanks(getChargeRanks(wardId, team.getId()));
            teamState.initShiftStates(getShiftStates(ward, team.getId(), year, month, day));
            teamStates.add(teamState);
        }
        return new DayState(teamStates);
    }

    private List<UUID> getChargeRanks(UUID wardId, UUID teamId) {
        List<Member> members = memberService.getMembersOfTeam(wardId, teamId);
        Collections.sort(members, (o1, o2) -> {
            return o1.getRank() - o2.getRank();
        });
        List<UUID> memberRanks = new ArrayList<>();
        for (Member member : members) {
            memberRanks.add(member.getUserId());
        }
        return memberRanks;
    }

    private List<ShiftState> getShiftStates(Ward ward, UUID teamId, int year, int month, int day) {
        List<ShiftState> shiftStates = new ArrayList<>();
        DayType dayType = DayTypeResolver.of(year, month, day);
        Map<UUID, Integer> shifts = ward.getRequirementsOfTeam(teamId);

        for (UUID shiftId : shifts.keySet()) {
            Shift shift = ward.getShift(shiftId);
            if (shift.getDayType() == dayType) {
                shiftStates.add(new ShiftState(shiftId, shifts.get(shiftId)));
            }
        }
        return shiftStates;
    }
}
