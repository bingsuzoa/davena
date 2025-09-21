package com.davena.organization.domain.service;

import com.davena.common.WardService;
import com.davena.common.MemberService;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.application.dto.ward.MemberDto;
import com.davena.organization.application.dto.ward.team.*;
import com.davena.organization.domain.model.ward.Team;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamMembersService {

    private final WardService existenceCheck;
    private final MemberService memberService;

    public static final String HAS_ANY_MEMBER_OF_TEAM = "팀에 멤버가 배정된 경우에는 팀을 삭제할 수 없어요. 멤버를 다른 팀으로 우선 옮겨주세요.";

    public TeamMembersResponse getTeamMembers(GetTeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisorOfWard(ward, request.supervisorId());
        return getTeamMembersDto(ward);
    }

    public TeamMembersResponse addNewTeam(CreateTeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisorOfWard(ward, request.supervisorId());
        ward.addNewTeam(request.name());
        return getTeamMembersDto(ward);
    }

    public TeamMembersResponse deleteTeam(DeleteTeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisorOfWard(ward, request.supervisorId());
        UUID teamId = request.teamId();
        validateTeamHasNoMembers(ward, teamId);
        ward.deleteTeam(teamId);
        return getTeamMembersDto(ward);
    }

    private void validateTeamHasNoMembers(Ward ward, UUID teamId) {
        List<Member> membersOfTeam = memberService.getMembersOfTeam(ward.getId(), teamId);
        if (!membersOfTeam.isEmpty()) {
            throw new IllegalArgumentException(HAS_ANY_MEMBER_OF_TEAM);
        }
    }

    public TeamMembersResponse updateTeamAssignments(UpdateTeamMembersRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisorOfWard(ward, request.supervisorId());
        memberService.validateAtLeastOneMember(request.usersOfTeam());
        memberService.validateContainAllMembers(ward, request.usersOfTeam());
        updateMembersTeam(request);
        return getTeamMembersDto(ward);
    }

    private void updateMembersTeam(UpdateTeamMembersRequest request) {
        Map<UUID, List<UUID>> usersOfTeam = request.usersOfTeam();
        for (UUID teamId : usersOfTeam.keySet()) {
            updateMemberTeam(teamId, usersOfTeam.get(teamId));
        }
    }

    private void updateMemberTeam(UUID teamId, List<UUID> userDtos) {
        for (UUID userId : userDtos) {
            Member member = memberService.getMember(userId);
            member.updateTeam(teamId);
        }
    }

    private TeamMembersResponse getTeamMembersDto(Ward ward) {
        List<Member> allMembers = memberService.getAllMembersOfWard(ward.getId());

        Map<UUID, List<MemberDto>> teamDtos = new HashMap<>();
        for (Team team : ward.getTeams()) {
            teamDtos.putIfAbsent(team.getId(), new ArrayList<>());
        }
        for (Member member : allMembers) {
            teamDtos.computeIfAbsent(member.getTeamId(), k -> new ArrayList<>())
                    .add(MemberDto.from(member));
        }
        Map<UUID, TeamDto> teamMembersMap = new HashMap<>();
        for (UUID teamId : teamDtos.keySet()) {
            Team team = ward.getTeam(teamId);
            teamMembersMap.computeIfAbsent(teamId, k -> new TeamDto(teamId, team.getName(), team.isDefault(), teamDtos.get(teamId)));
        }
        return new TeamMembersResponse(ward.getId(), ward.getSupervisorId(), teamMembersMap);
    }
}
