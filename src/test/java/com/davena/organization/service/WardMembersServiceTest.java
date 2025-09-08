package com.davena.organization.service;

import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.user.JoinStatus;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import com.davena.organization.domain.service.ExistenceService;
import com.davena.organization.domain.service.WardJoinService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WardJoinServiceTest {

    @Mock
    private ExistenceService existenceCheck;

    @Mock
    private WardRepository wardRepository;

    @InjectMocks
    private WardJoinService wardJoinService;

    @Test
    @DisplayName("입력한 토큰과 일치하는 병동이 있으면 반환")
    void findWardByToken() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(wardRepository.findByToken(any())).thenReturn(Optional.of(ward));
        WardResponse response = wardJoinService.findWardByToken(ward.getToken());
        Assertions.assertEquals(ward.getId(), response.wardId());
    }

    @Test
    @DisplayName("병동 가입 신청 시 USER 상태 PENDING으로 변환")
    void applyForWard() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = wardJoinService.applyForWard(new JoinRequest(user.getId(), supervisorId, ward.getId()));
        Assertions.assertEquals(response.status(), JoinStatus.PENDING);
    }

    @Test
    @DisplayName("병동 가입 승인 시 USER 상태 APPROVED")
    void approveJoinRequest() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = wardJoinService.approveJoinRequest(new JoinRequest(user.getId(), supervisorId, ward.getId()));
        Assertions.assertEquals(response.status(), JoinStatus.APPROVE);
        Assertions.assertEquals(response.wardId(), ward.getId());
    }

    @Test
    @DisplayName("병동 가입 승인 시 USER는 DefaultTeam에 배정된다.")
    void approveJoinRequest_DefaultTeam_배정확인() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        wardJoinService.approveJoinRequest(new JoinRequest(user.getId(), supervisorId, ward.getId()));
        Assertions.assertTrue(ward.getTeams().getFirst().isDefault());

        UUID teamId = ward.getTeams().getFirst().id();
        List<UUID> users = ward.getUsersOfTeam(teamId);
        Assertions.assertEquals(users.size(), 1);
    }

    @Test
    @DisplayName("병동 가입 거절 시 USER 상태 NONE")
    void rejectJoinRequest() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        JoinResponse response = wardJoinService.rejectJoinRequest(new JoinRequest(user.getId(), supervisorId, ward.getId()));
        Assertions.assertEquals(user.getStatus(), JoinStatus.NONE);
        Assertions.assertEquals(response.status(), JoinStatus.NONE);
        Assertions.assertEquals(response.wardId(), null);
    }

    /// /// 예외 테스트

    @Test
    @DisplayName("입력한 토큰과 일치하는 병동이 없으면 예외")
    void findWardByToken_exception() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(wardRepository.findByToken(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wardJoinService.findWardByToken(ward.getToken());
        });
    }

    @Test
    @DisplayName("병동의 supervisor가 아닌 사용자가 승인 시 예외")
    void approveJoinRequest_exception() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID notSupervisorId = UUID.randomUUID();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(existenceCheck.verifySupervisor(ward, notSupervisorId)).thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wardJoinService.approveJoinRequest(new JoinRequest(user.getId(), notSupervisorId, ward.getId()));
        });
    }

    @Test
    @DisplayName("병동의 supervisor가 아닌 사용자가 거절 시 예외")
    void rejectJoinRequest_exception() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID notSupervisorId = UUID.randomUUID();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(existenceCheck.verifySupervisor(ward, notSupervisorId)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wardJoinService.rejectJoinRequest(new JoinRequest(user.getId(), notSupervisorId, ward.getId()));
        });
    }

}
