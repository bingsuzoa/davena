package com.davena.organization.domain.service;

import com.davena.common.WardService;
import com.davena.organization.application.dto.ward.shiftRequirement.GetWardRequirementsRequest;
import com.davena.organization.application.dto.ward.shiftRequirement.RequirementShiftDto;
import com.davena.organization.application.dto.ward.shiftRequirement.TeamRequirementsDto;
import com.davena.organization.application.dto.ward.shiftRequirement.WardRequirementsDto;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Team;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequirementsService {

    private final WardService wardService;

    public WardRequirementsDto getRequirements(GetWardRequirementsRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, request.supervisorId());
        return getWardRequirementsDto(ward);
    }

    public WardRequirementsDto updateWardRequirements(WardRequirementsDto request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, request.supervisorId());

        for (TeamRequirementsDto teamRequirements : request.requirements()) {
            updateTeamRequirements(teamRequirements, ward);
        }
        return getWardRequirementsDto(ward);
    }

    private void updateTeamRequirements(TeamRequirementsDto teamRequirements, Ward ward) {
        List<RequirementShiftDto> requirementsDto = teamRequirements.requirements();
        for (RequirementShiftDto shiftDto : requirementsDto) {
            ward.updateRequirement(teamRequirements.teamId(), shiftDto.shiftId(), shiftDto.requiredCount());
        }
    }

    private WardRequirementsDto getWardRequirementsDto(Ward ward) {
        Map<UUID, Map<UUID, Integer>> wardRequirements = ward.getRequirements();

        List<TeamRequirementsDto> teamRequirementsDtos = new ArrayList<>();
        for (UUID teamId : wardRequirements.keySet()) {
            Team team = ward.getTeam(teamId);
            teamRequirementsDtos.add(new TeamRequirementsDto(teamId, team.getName(), getTeamRequirementsDto(ward, wardRequirements.get(teamId))));
        }
        return new WardRequirementsDto(ward.getId(), ward.getSupervisorId(), teamRequirementsDtos);
    }

    private List<RequirementShiftDto> getTeamRequirementsDto(Ward ward, Map<UUID, Integer> teamRequirements) {
        List<RequirementShiftDto> requirements = new ArrayList<>();
        for (UUID shiftId : teamRequirements.keySet()) {
            Shift shift = ward.getShift(shiftId);
            requirements.add(new RequirementShiftDto(shiftId, shift.getDayType(), shift.getName(), teamRequirements.get(shiftId)));
        }
        return requirements;
    }
}
