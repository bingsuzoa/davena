package com.davena.organization.service;

import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.hospital.HospitalId;
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
