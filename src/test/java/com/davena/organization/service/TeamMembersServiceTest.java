package com.davena.organization.service;

import com.davena.common.WardService;
import com.davena.common.MemberService;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.application.dto.ward.MemberDto;
import com.davena.organization.application.dto.ward.team.*;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Team;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.service.TeamMembersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.davena.organization.domain.service.TeamMembersService.HAS_ANY_MEMBER_OF_TEAM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMembersServiceTest {

    private WardService existenceCheck = mock(WardService.class);
    private MemberService memberService = mock(MemberService.class);
    private TeamMembersService teamMembersService = new TeamMembersService(existenceCheck, memberService);

    @Test
    @DisplayName("팀 배정 현황 조회")
    void getTeamMembers() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        Team team = ward.getTeams().getFirst();
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisorOfWard(any(), any())).thenReturn(true);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateTeam(team.getId());

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.updateTeam(team.getId());

        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2));
        TeamMembersResponse response = teamMembersService.getTeamMembers(new GetTeamRequest(ward.getId(), ward.getSupervisorId()));

        List<MemberDto> memberDtos = response.teamMembers().get(team.getId()).members();
        Assertions.assertEquals(2, memberDtos.size());
    }

    @Test
    @DisplayName("새로운 팀 생성")
    void addNewTeam() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisorOfWard(any(), any())).thenReturn(true);

        CreateTeamRequest request = new CreateTeamRequest(ward.getId(), ward.getSupervisorId(), "bTeam");
        TeamMembersResponse response = teamMembersService.addNewTeam(request);
        Assertions.assertEquals(2, response.teamMembers().size());
    }

    @Test
    @DisplayName("기존 팀 삭제 기능")
    void deleteTeam() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisorOfWard(any(), any())).thenReturn(true);
        UUID bTeamId = ward.addNewTeam("bTeam");
        Assertions.assertEquals(2, ward.getTeams().size());

        TeamMembersResponse response = teamMembersService.deleteTeam(new DeleteTeamRequest(ward.getId(), ward.getSupervisorId(), bTeamId));
        Assertions.assertEquals(1, response.teamMembers().size());
    }

    @Test
    @DisplayName("병동 멤버 팀 변경")
    void updateTeamAssignments() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisorOfWard(any(), any())).thenReturn(true);
        UUID aTeamId = ward.getTeams().getFirst().getId();
        UUID bTeamId = ward.addNewTeam("bTeam");

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        when(memberService.getMember(user2.getId())).thenReturn(member2);

        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2));

        Map<UUID, List<UUID>> teams = new HashMap<>();
        teams.put(aTeamId, List.of(user1.getId()));
        teams.put(bTeamId, List.of(user2.getId()));
        UpdateTeamMembersRequest request = new UpdateTeamMembersRequest(ward.getId(), ward.getSupervisorId(), teams);
        Map<UUID, TeamDto> teamDtos = teamMembersService.updateTeamAssignments(request).teamMembers();

        Assertions.assertEquals(member2.getUserId(), teamDtos.get(bTeamId).members().getFirst().id());
    }

    /// ///예외 테스트
    @Test
    @DisplayName("이미 존재하는 팀과 동일한 이름으로 생성할 경우 예외 발생")
    void addNewTeam_동일_이름_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisorOfWard(any(), any())).thenReturn(true);

        CreateTeamRequest request = new CreateTeamRequest(ward.getId(), ward.getSupervisorId(), Team.DEFAULT_TEAM);
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> teamMembersService.addNewTeam(request)
        );
        Assertions.assertEquals(Ward.ALREADY_EXIST_TEAM_NAME, e.getMessage());
    }

    @Test
    @DisplayName("default Team 삭제 시도 시 예외 발생")
    void deleteTeam_default_team_삭제_불가() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisorOfWard(any(), any())).thenReturn(true);

        UUID teamId = ward.getTeams().getFirst().getId();
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> teamMembersService.deleteTeam(new DeleteTeamRequest(ward.getId(), ward.getSupervisorId(), teamId))
        );
        Assertions.assertEquals(Team.CAN_NOT_REMOVE_DEFAULT_TEAM, e.getMessage());
    }

    @Test
    @DisplayName("team삭제 시도 시 배정된 멤버가 존재할 경우 예외 발생")
    void deleteTeam_배정된_멤버_있으면_삭제_불가() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisorOfWard(any(), any())).thenReturn(true);

        UUID teamId = ward.addNewTeam("bTeam");
        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateTeam(teamId);
        when(memberService.getMembersOfTeam(any(), any())).thenReturn(List.of(member1));
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> teamMembersService.deleteTeam(new DeleteTeamRequest(ward.getId(), ward.getSupervisorId(), teamId))
        );
        Assertions.assertEquals(HAS_ANY_MEMBER_OF_TEAM, e.getMessage());
    }
}
