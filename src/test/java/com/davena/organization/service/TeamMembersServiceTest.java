package com.davena.organization.service;

import com.davena.constraint.domain.model.Member;
import com.davena.organization.application.dto.ward.team.TeamMembersRequest;
import com.davena.organization.application.dto.ward.team.TeamMembersResponse;
import com.davena.organization.application.dto.ward.team.TeamRequest;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.service.TeamMembersService;
import com.davena.common.ExistenceService;
import com.davena.organization.domain.service.util.Mapper;
import com.davena.organization.domain.service.util.MembersValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.davena.organization.domain.model.ward.Team.CAN_NOT_REMOVE_DEFAULT_TEAM;
import static com.davena.organization.domain.model.ward.Team.HAS_ANY_MEMBER_OF_TEAM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamMembersServiceTest {

    private ExistenceService existenceCheck = mock(ExistenceService.class);
    private MembersValidator membersValidator = mock(MembersValidator.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private Mapper mapper = new Mapper();

    private TeamMembersService teamMembersService =
            new TeamMembersService(existenceCheck, membersValidator, userRepository, mapper);


    @Test
    @DisplayName("병동에서 팀 추가 확인")
    void addNewTeam() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        TeamRequest request = new TeamRequest(null, ward.getSupervisorId(), ward.getId(), "B팀");

        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        TeamMembersResponse response = teamMembersService.addNewTeam(request);

        Assertions.assertEquals("B팀", ward.getTeams().getLast().getName());
        Assertions.assertFalse(ward.getTeams().getLast().isDefault());
    }

    @Test
    @DisplayName("Team에 Member 배정하기")
    void updateTeamAssignments() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID aTeamId = ward.getTeams().getFirst().getId();
        UUID bTeamId = ward.addNewTeam("B팀");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        ward.addNewUser(user1.getId());
        when(existenceCheck.getMember(user1.getId())).thenReturn(new Member(user1.getId(), ward.getId(), user1.getName()));
        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        ward.addNewUser(user2.getId());
        when(existenceCheck.getMember(user2.getId())).thenReturn(new Member(user2.getId(), ward.getId(), user2.getName()));
        User user3 = User.create("name3", "loginId3", "password", "01011112224");
        ward.addNewUser(user3.getId());
        when(existenceCheck.getMember(user3.getId())).thenReturn(new Member(user3.getId(), ward.getId(), user3.getName()));
        User user4 = User.create("name4", "loginId4", "password", "01011112225");
        ward.addNewUser(user4.getId());
        when(existenceCheck.getMember(user4.getId())).thenReturn(new Member(user4.getId(), ward.getId(), user4.getName()));
        User user5 = User.create("name5", "loginId5", "password", "01011112226");
        ward.addNewUser(user5.getId());
        when(existenceCheck.getMember(user5.getId())).thenReturn(new Member(user5.getId(), ward.getId(), user5.getName()));

        when(userRepository.findAllById(any())).thenReturn(List.of(user1, user2, user3, user4, user5));

        doNothing().when(membersValidator).validateAtLeastOneMember(any());
        doNothing().when(membersValidator).validateContainAllMembers(any(), any());

        Map<UUID, List<UUID>> map = new HashMap<>();
        map.put(aTeamId, List.of(user1.getId(), user2.getId()));
        map.put(bTeamId, List.of(user3.getId(), user4.getId(), user5.getId()));

        teamMembersService.updateTeamAssignments(new TeamMembersRequest(ward.getSupervisorId(), ward.getId(), map));
        Assertions.assertEquals(ward.getUsersOfTeam(bTeamId).size(), 3);
        Assertions.assertEquals(ward.getUsersOfTeam(aTeamId).size(), 2);
    }

    @Test
    @DisplayName("Member 객체에도 TeamId 배정되는지 확인")
    void updateTeamAssignments_실제Member객체_배정_확인() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID aTeamId = ward.getTeams().getFirst().getId();
        UUID bTeamId = ward.addNewTeam("B팀");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        ward.addNewUser(user1.getId());
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        when(existenceCheck.getMember(user1.getId())).thenReturn(member1);

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        ward.addNewUser(user2.getId());
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        when(existenceCheck.getMember(user2.getId())).thenReturn(member2);

        User user3 = User.create("name3", "loginId3", "password", "01011112224");
        ward.addNewUser(user3.getId());
        Member member3 = new Member(user3.getId(), ward.getId(), user3.getName());
        when(existenceCheck.getMember(user3.getId())).thenReturn(member3);

        when(userRepository.findAllById(any())).thenReturn(List.of(user1, user2, user3));

        doNothing().when(membersValidator).validateAtLeastOneMember(any());
        doNothing().when(membersValidator).validateContainAllMembers(any(), any());

        Map<UUID, List<UUID>> map = new HashMap<>();
        map.put(aTeamId, List.of(user1.getId(), user2.getId()));
        map.put(bTeamId, List.of(user3.getId()));
        teamMembersService.updateTeamAssignments(new TeamMembersRequest(ward.getSupervisorId(), ward.getId(), map));

        Assertions.assertEquals(member1.getTeamId(), aTeamId);
        Assertions.assertEquals(member3.getTeamId(), bTeamId);
    }

    @Test
    @DisplayName("팀 삭제")
    void deleteTeam() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        UUID aTeamId = ward.getTeams().getFirst().getId();
        UUID bTeamId = ward.addNewTeam("B팀");

        User user1 = User.create("user1", "user1", "1111", "01011111111");
        UUID user1Id = user1.getId();
        ward.addNewUser(user1Id);
        User user2 = User.create("user2", "user2", "1112", "01011111112");
        UUID user2Id = user2.getId();
        ward.addNewUser(user2Id);
        User user3 = User.create("user3", "user3", "1113", "01011111113");
        UUID user3Id = user3.getId();
        ward.addNewUser(user3Id);

        List<User> users = List.of(user1, user2, user3);
        when(userRepository.findAllById(any())).thenReturn(users);

        TeamRequest request = new TeamRequest(bTeamId, ward.getSupervisorId(), ward.getId(), ward.getName());
        TeamMembersResponse dto = teamMembersService.deleteTeam(request);
        Assertions.assertEquals(dto.usersOfTeam().size(), 1);
    }

    /// ///예외 테스트
    @Test
    @DisplayName("supervisor아닌 사용자가 시도 시 예외 발생")
    void addNewTeam_exception() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        TeamRequest request = new TeamRequest(null, ward.getSupervisorId(), ward.getId(), "B팀");

        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            teamMembersService.addNewTeam(request);
        });
    }

    @Test
    @DisplayName("팀 삭제할 때, 팀에 멤버가 존재할 경우 예외 발생")
    void deleteTeam_exception() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        UUID aTeamId = ward.getTeams().getFirst().getId();
        UUID bTeamId = ward.addNewTeam("B팀");

        User user1 = User.create("user1", "user1", "1111", "01011111111");
        UUID user1Id = user1.getId();
        ward.addNewUser(user1Id);
        User user2 = User.create("user2", "user2", "1112", "01011111112");
        UUID user2Id = user2.getId();
        ward.addNewUser(user2Id);
        User user3 = User.create("user3", "user3", "1113", "01011111113");
        UUID user3Id = user3.getId();
        ward.addNewUser(user3Id);

        TeamRequest request = new TeamRequest(bTeamId, ward.getSupervisorId(), ward.getId(), ward.getName());
        ward.setUsersToTeam(aTeamId, List.of(user1Id, user2Id));
        ward.setUsersToTeam(bTeamId, List.of(user3Id));

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            teamMembersService.deleteTeam(request);
        });

        Assertions.assertEquals(e.getMessage(), HAS_ANY_MEMBER_OF_TEAM);
    }

    @Test
    @DisplayName("팀 삭제할 때, default팀 삭제 시도 시 예외 발생")
    void deleteTeam_exception2() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        UUID aTeamId = ward.getTeams().getFirst().getId();
        UUID bTeamId = ward.addNewTeam("B팀");

        User user1 = User.create("user1", "user1", "1111", "01011111111");
        UUID user1Id = user1.getId();
        ward.addNewUser(user1Id);
        User user2 = User.create("user2", "user2", "1112", "01011111112");
        UUID user2Id = user2.getId();
        ward.addNewUser(user2Id);
        User user3 = User.create("user3", "user3", "1113", "01011111113");
        UUID user3Id = user3.getId();
        ward.addNewUser(user3Id);

        TeamRequest request = new TeamRequest(aTeamId, ward.getSupervisorId(), ward.getId(), ward.getName());
        ward.clearAllTeamMembers();
        ward.setUsersToTeam(bTeamId, List.of(user1Id, user2Id, user3Id));

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            teamMembersService.deleteTeam(request);
        });

        Assertions.assertEquals(CAN_NOT_REMOVE_DEFAULT_TEAM, e.getMessage());
    }
}
