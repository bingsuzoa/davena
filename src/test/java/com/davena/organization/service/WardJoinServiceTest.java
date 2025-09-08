package com.davena.organization.service;

import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.application.dto.ward.team.TeamDto;
import com.davena.organization.application.dto.ward.team.TeamMembersDto;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.user.JoinStatus;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.TeamId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import com.davena.organization.domain.service.ExistenceService;
import com.davena.organization.domain.service.JoinService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JoinServiceTest {

    @Mock
    private ExistenceService existenceCheck;

    @Mock
    private WardRepository wardRepository;

    @InjectMocks
    private JoinService joinService;

    @Test
    @DisplayName("입력한 토큰과 일치하는 병동이 있으면 반환")
    void findWardByToken() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        when(wardRepository.findByToken(any())).thenReturn(Optional.of(ward));
        WardResponse response = joinService.findWardByToken(ward.getToken());
        Assertions.assertEquals(ward.getId().id(), response.wardId());
    }

    @Test
    @DisplayName("병동 가입 신청 시 USER 상태 PENDING으로 변환")
    void applyForWard() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        UserId supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = joinService.applyForWard(new JoinRequest(user.getId().id(), supervisorId.id(), ward.getId().id()));
        Assertions.assertEquals(response.status(), JoinStatus.PENDING);
    }

    @Test
    @DisplayName("병동 가입 승인 시 USER 상태 APPROVED")
    void approveJoinRequest() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        UserId supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = joinService.approveJoinRequest(new JoinRequest(user.getId().id(), supervisorId.id(), ward.getId().id()));
        Assertions.assertEquals(response.status(), JoinStatus.APPROVE);
        Assertions.assertEquals(response.wardId(), ward.getId().id());
    }

    @Test
    @DisplayName("병동 가입 거절 시 USER 상태 NONE, 프론트 반환 상태는 REJECTED")
    void rejectJoinRequest() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        UserId supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = joinService.rejectJoinRequest(new JoinRequest(user.getId().id(), supervisorId.id(), ward.getId().id()));
        Assertions.assertEquals(user.getStatus(), JoinStatus.NONE);
        Assertions.assertEquals(response.status(), JoinStatus.REJECTED);
        Assertions.assertEquals(response.wardId(), ward.getId().id());
    }

    @Test
    @DisplayName("Team에 Member 배정하기")
    void updateMembersOfTeam() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        ward.addNewTeam("B팀");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);

        UUID user1 = UUID.randomUUID();
        ward.addNewUser(new UserId(user1));
        UUID user2 = UUID.randomUUID();
        ward.addNewUser(new UserId(user2));
        UUID user3 = UUID.randomUUID();
        ward.addNewUser(new UserId(user3));
        UUID user4 = UUID.randomUUID();
        ward.addNewUser(new UserId(user4));
        UUID user5 = UUID.randomUUID();
        ward.addNewUser(new UserId(user5));

        Map<UUID, List<UUID>> map = new HashMap<>();

        List<TeamDto> teams = ward.getTeams();
        map.put(teams.get(0).id().id(), List.of(user1, user2, user3));
        map.put(teams.get(1).id().id(), List.of(user4, user5));

        TeamId teamId = teams.get(0).id();
        joinService.updateMembersOfTeam(new TeamMembersDto(ward.getSupervisorId().id(), ward.getId().id(), map));
        Assertions.assertEquals(ward.getUsersOfTeam(teamId).size(), 3);
    }

    /// /// 예외 테스트

    @Test
    @DisplayName("입력한 토큰과 일치하는 병동이 없으면 예외")
    void findWardByToken_exception() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        when(wardRepository.findByToken(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            joinService.findWardByToken(ward.getToken());
        });
    }

    @Test
    @DisplayName("병동의 supervisor가 아닌 사용자가 승인 시 예외")
    void approveJoinRequest_exception() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        UserId notSupervisorId = new UserId(UUID.randomUUID());
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(existenceCheck.verifySupervisor(ward, notSupervisorId)).thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            joinService.approveJoinRequest(new JoinRequest(user.getId().id(), notSupervisorId.id(), ward.getId().id()));
        });
    }

    @Test
    @DisplayName("병동의 supervisor가 아닌 사용자가 거절 시 예외")
    void rejectJoinRequest_exception() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        UserId notSupervisorId = new UserId(UUID.randomUUID());
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(existenceCheck.verifySupervisor(ward, notSupervisorId)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            joinService.rejectJoinRequest(new JoinRequest(user.getId().id(), notSupervisorId.id(), ward.getId().id()));
        });
    }

}
