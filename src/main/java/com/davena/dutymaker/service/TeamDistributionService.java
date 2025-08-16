package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.TeamBox;
import com.davena.dutymaker.api.dto.TeamDistributionRequest;
import com.davena.dutymaker.domain.organization.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.davena.dutymaker.domain.organization.Team.NOT_MATCH_WARD_MEMBERS_COUNT;

@Service
@RequiredArgsConstructor
public class TeamDistributionService {

    private final MemberService memberService;
    private final WardService wardService;
    private final TeamService teamService;

    public void updateTeamDistribution(Long wardId, TeamDistributionRequest request) {
        matchesWardMembersCount(wardId, request);
        initTeam(wardId);
        List<TeamBox> teams = request.teams();

        Ward ward = wardService.getWard(wardId);
        for (TeamBox team : teams) {
            updateTeamOfMember(ward, team);
        }
    }

    private void initTeam(Long wardId) {
        Set<Member> members = wardService.getWardWithMembers(wardId).getMembers();
        for (Member member : members) {
            member.initTeam();
        }
        teamService.deleteTeamOfWard(wardId);
    }

    private void updateTeamOfMember(Ward ward, TeamBox teambox) {
        List<Long> members = teambox.members();
        Optional<Team> optionalTeam = teamService.getTeam(teambox.teamId());
        if (optionalTeam.isEmpty()) {
            Team team = teamService.saveTeam(new Team(ward, teambox.name()));
            for (Long id : members) {
                Member member = memberService.getMember(id);
                member.changeTeam(team);
            }
            return;
        }
        Team team = optionalTeam.get();
        for (Long id : members) {
            Member member = memberService.getMember(id);
            member.changeTeam(team);
        }
    }

    private void matchesWardMembersCount(Long wardId, TeamDistributionRequest request) {
        Long totalMemberCount = memberService.countMemberByWard(wardId);
        List<TeamBox> teams = request.teams();
        Long count = 0L;
        for (TeamBox team : teams) {
            count += team.members().size();
        }
        if (totalMemberCount != count) {
            throw new IllegalArgumentException(NOT_MATCH_WARD_MEMBERS_COUNT);
        }
    }
}
