package com.davena.organization.service;

import com.davena.organization.application.dto.WardRequest;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import com.davena.organization.domain.service.CreateWardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CreateWardTest {

    @Mock
    private WardRepository wardRepository;

    @InjectMocks
    private CreateWardService createWardService;

}
