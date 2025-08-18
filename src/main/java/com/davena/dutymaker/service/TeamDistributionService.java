package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.team.TeamBox;
import com.davena.dutymaker.api.dto.team.TeamDistributionRequest;
import com.davena.dutymaker.api.dto.team.TeamUpdateRequest;
import com.davena.dutymaker.domain.organization.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.TeamRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.davena.dutymaker.domain.organization.Team.NOT_MATCH_TEAM_WITH_WARD_MEMBERS_COUNT;

@Service
@RequiredArgsConstructor
public class TeamDistributionService {

    private final MemberRepository memberRepository;
    private final WardRepository wardRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public void updateTeam(Long wardId, Long teamId, TeamUpdateRequest request) {
        Optional<Team> optionalTeam = teamRepository.findByWardIdAndId(wardId, teamId);
        if(optionalTeam.isEmpty()) {
            throw new IllegalArgumentException(Team.NOT_EXIST_TEAM);
        }
        Team team = optionalTeam.get();
        team.updateName(request.name());
    }

    @Transactional
    public void deleteTeam(Long wardId, Long teamId) {
        Optional<Team> optionalTeam = teamRepository.findByWardIdAndId(wardId, teamId);
        if(optionalTeam.isEmpty()) {
            throw new IllegalArgumentException(Team.NOT_EXIST_TEAM);
        }
        if(optionalTeam.get().isDefault()) {
            throw new IllegalArgumentException(Team.CANNOT_DELETE_DEFAULT_TEAM);
        }
        Team team = optionalTeam.get();
        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(wardId).get();
        teamRepository.reassignMembers(team, defaultTeam);
        teamRepository.delete(team);
    }

    public void updateTeamDistribution(Long wardId, TeamDistributionRequest request) {
        matchesWardMembersCount(wardId, request);
        Ward ward = getWard(wardId);
        for (TeamBox teamBox : request.teams()) {
            if (teamBox.teamId() == null) {
                updateTeamOfMembers(teamBox.members(), createTeam(ward, teamBox.name()));
            } else {
                updateTeamOfMembers(teamBox.members(), getTeam(teamBox.teamId()));
            }
        }
        deleteUnusedTeams(wardId);
    }

    private void matchesWardMembersCount(Long wardId, TeamDistributionRequest request) {
        Long totalMemberCount = memberRepository.countByWardId(wardId);
        List<TeamBox> teams = request.teams();
        Long count = 0L;
        for (TeamBox team : teams) {
            count += team.members().size();
        }
        if (totalMemberCount != count) {
            throw new IllegalArgumentException(NOT_MATCH_TEAM_WITH_WARD_MEMBERS_COUNT);
        }
    }

    private void deleteUnusedTeams(Long wardId) {
        wardRepository.deleteEmptyTeams(wardId);
    }

    private Team createTeam(Ward ward, String name) {
        return teamRepository.save(new Team(ward, name));
    }

    private void updateTeamOfMembers(List<Long> members, Team team) {
        for (Long memberId : members) {
            Member member = getMember(memberId);
            member.updateTeam(team);
        }
    }

    private Ward getWard(Long wardId) {
        return wardRepository.findById(wardId).orElseThrow(() -> new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() ->
                new IllegalArgumentException(Team.NOT_EXIST_TEAM));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException(Member.NOT_EXIST_MEMBER));
    }

}
