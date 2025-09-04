package com.davena.organization.service;

import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.user.JoinStatus;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.user.UserId;
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

import java.util.Optional;
import java.util.UUID;

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
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = joinService.applyForWard(new JoinRequest(user.getId().id(), ward.getId().id()));
        Assertions.assertEquals(response.status(), JoinStatus.PENDING);
    }

    @Test
    @DisplayName("병동 가입 승인 시 USER 상태 APPROVED")
    void approveJoinRequest() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = joinService.approveJoinRequest(new JoinRequest(user.getId().id(), ward.getId().id()));
        Assertions.assertEquals(response.status(), JoinStatus.APPROVE);
        Assertions.assertEquals(response.wardId(), ward.getId().id());
    }

    @Test
    @DisplayName("병동 가입 거절 시 USER 상태 NONE, 프론트 반환 상태는 REJECTED")
    void rejectJoinRequest() {
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동", UUID.randomUUID().toString());
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = joinService.rejectJoinRequest(new JoinRequest(user.getId().id(), ward.getId().id()));
        Assertions.assertEquals(user.getStatus(), JoinStatus.NONE);
        Assertions.assertEquals(response.status(), JoinStatus.REJECTED);
        Assertions.assertEquals(response.wardId(), ward.getId().id());
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
}
