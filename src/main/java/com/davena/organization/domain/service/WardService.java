package com.davena.organization.domain.service;

import com.davena.organization.application.dto.ward.WardRequest;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository wardRepository;

    public WardResponse createWard(WardRequest request) {
        Ward ward = Ward.create(
                new HospitalId(request.hospitalId()),
                new UserId(request.supervisorId()),
                request.wardName(),
                createToken()
        );
        return WardResponse.from(wardRepository.save(ward));
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }
}
