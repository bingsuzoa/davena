package com.davena.organization.domain.service;

import com.davena.organization.application.dto.ward.shiftRequirement.RequirementDto;
import com.davena.organization.application.dto.ward.shiftRequirement.RequirementRequest;
import com.davena.organization.application.dto.ward.shiftRequirement.RequirementsResponse;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.service.util.ExistenceService;
import com.davena.organization.domain.service.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequirementsService {

    private final ExistenceService existenceService;
    private final Mapper mapper;

    public RequirementsResponse getRequirements(RequirementRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        List<RequirementDto> requirementDtos = mapper.getRequirementDtos(ward.getRequirements(), ward.getTeams());
        return new RequirementsResponse(ward.getSupervisorId(), ward.getId(), requirementDtos);
    }

    public RequirementsResponse updateRequirement(RequirementRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        ward.updateRequirement(request.teamId(), request.dayType(), request.shiftId(), request.updatedRequirement());
        List<RequirementDto> requirementDtos = mapper.getRequirementDtos(ward.getRequirements(), ward.getTeams());
        return new RequirementsResponse(ward.getSupervisorId(), ward.getId(), requirementDtos);
    }
}
