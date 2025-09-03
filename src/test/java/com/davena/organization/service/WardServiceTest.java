package com.davena.organization.service;

import com.davena.organization.application.dto.WardRequest;
import com.davena.organization.application.dto.WardResponse;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import com.davena.organization.domain.service.CreateWardService;
import com.davena.organization.infrastructure.adapter.JpaWardRepositoryImpl;
import com.davena.organization.infrastructure.entity.WardEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateWardTest {

    @Mock
    private WardRepository wardRepository;

    @InjectMocks
    private JpaWardRepositoryImpl adapter;

    @InjectMocks
    private CreateWardService createWardService;

    @Test
    @DisplayName("병동 생성 확인")
    void Ward_생성_확인() {
        WardRequest request = new WardRequest(UUID.randomUUID(), UUID.randomUUID(), "외상 병동");
        Ward ward = Ward.create(new HospitalId(UUID.randomUUID()), new UserId(UUID.randomUUID()), "외상 병동");
        when(wardRepository.save(any())).thenReturn(ward);
        WardResponse response = createWardService.createWard(request);
        Assertions.assertNotNull(response.wardId());
    }
}
