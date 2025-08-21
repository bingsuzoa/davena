package com.davena.dutymaker.service.generator;

import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.organization.team.TeamState;
import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.RequirementRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamStateService {

    private final RequirementRuleRepository ruleRepository;
    private final MemberRepository memberRepository;

    public Map<Long, TeamState> initTeamState(Schedule schedule, Ward ward) {
        YearMonth thisMonth = YearMonth.parse(schedule.getYearMonth());

        Map<Long, TeamState> teamStates = new HashMap<>();
        for(Team team : ward.getTeams()) {
            Map<ShiftType, Integer> remain = new HashMap<>();

            for(int day = 1; day <= thisMonth.lengthOfMonth(); day++) {
                DayType dayType = DayType.from(LocalDate.of(thisMonth.getYear(), thisMonth.getMonth(), day));
                List<RequirementRule> rules = ruleRepository.findByTeamIdAndDayType(team.getId(), dayType);
                for(RequirementRule rule : rules) {
                    remain.putIfAbsent(rule.getShiftType(), rule.getRequired());
                }
            }
            teamStates.put(team.getId(), new TeamState(remain, getChargeOrder(team.getId())));
        }
        return teamStates;
    }


    private PriorityQueue<Member> getChargeOrder(Long teamId) {
        List<Member> members = memberRepository.findMembersWithTeamByWardId(teamId);
        return new PriorityQueue<>(members);
    }
}
