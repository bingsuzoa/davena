package com.davena.organization.domain.service;

import com.davena.organization.application.dto.WardRequest;
import com.davena.organization.application.dto.WardResponse;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.member.MemberId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateWardService {

    private final WardRepository wardRepository;

    public WardResponse createWard(WardRequest request) {
        Ward ward = Ward.create(new HospitalId(request.hospitalId()), new MemberId(request.memberId()), request.wardName());
        return WardResponse.from(wardRepository.save(ward));
    }
}
