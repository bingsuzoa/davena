package com.davena.dutymaker.service;

import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.organization.team.TeamState;
import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.RequirementRuleRepository;
import com.davena.dutymaker.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final RequirementRuleRepository ruleRepository;
    private final MemberRepository memberRepository;

    public List<TeamState> getTeamState(Long teamId, YearMonth yearMonth) {
        Team team = getTeam(teamId);
        List<RequirementRule> rulesOfTeam = ruleRepository.findByTeamId(teamId);

        Map<ShiftType, Integer> remain = new HashMap<>();
        for (RequirementRule rule : rulesOfTeam) {
            remain.putIfAbsent(rule.getShiftType(), rule.getRequired());
        }

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<TeamState> teamStates = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            int day = d.getDayOfMonth();
            teamStates.add(new TeamState(day, team, remain, getChargeOrder(teamId)));
        }
        return teamStates;
    }

    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() ->
                new IllegalArgumentException(Team.NOT_EXIST_TEAM));
    }

    public PriorityQueue<Member> getChargeOrder(Long teamId) {
        List<Member> members = memberRepository.findMembersWithTeam(teamId);
        return new PriorityQueue<>(members);
    }
}
