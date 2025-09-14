package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.constraint.application.dto.availabiltyRequest.GetWardAvailabilityRequest;
import com.davena.constraint.application.dto.availabiltyRequest.WardAvailabilityResponse;
import com.davena.constraint.domain.model.AvailabilityRequest;
import com.davena.constraint.domain.port.AvailabilityRequestRepository;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityRequestService {

    private final AvailabilityRequestRepository requestRepository;
    private final ExistenceService existenceService;

    public WardAvailabilityResponse getAllMembersRequest(GetWardAvailabilityRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        List<AvailabilityRequest> allRequests = requestRepository.getAllMembersRequest(ward.getId(), request.month());
    }
}
