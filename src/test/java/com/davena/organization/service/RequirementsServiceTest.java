package com.davena.organization.service;

import com.davena.common.ExistenceService;
import com.davena.organization.application.dto.ward.shiftRequirement.GetWardRequirementsRequest;
import com.davena.organization.application.dto.ward.shiftRequirement.RequirementShiftDto;
import com.davena.organization.application.dto.ward.shiftRequirement.TeamRequirementsDto;
import com.davena.organization.application.dto.ward.shiftRequirement.WardRequirementsDto;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Team;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.service.RequirementsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequirementsServiceTest {

    @Mock
    private ExistenceService existenceService;

    @InjectMocks
    private RequirementsService requirementsService;

    @Test
    @DisplayName("병동의 각 근무유형별 필요 인원 조회하기 = 처음에는 0으로 세팅 확인")
    void getRequirements() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        WardRequirementsDto response = requirementsService.getRequirements(new GetWardRequirementsRequest(ward.getId(), ward.getSupervisorId()));
        List<RequirementShiftDto> wardRequirements = response.requirements().getFirst().requirements();
        for (RequirementShiftDto shiftDto : wardRequirements) {
            Assertions.assertEquals(0, shiftDto.requiredCount());
        }
    }

    @Test
    @DisplayName("병동 근무유형별 업데이트 하기")
    void updateWardRequirements() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        List<RequirementShiftDto> shiftDtos = new ArrayList<>();
        for (Shift shift : ward.getShifts()) {
            shiftDtos.add(new RequirementShiftDto(shift.getId(), shift.getDayType(), shift.getName(), 2));
        }
        Team team = ward.getTeams().getFirst();
        TeamRequirementsDto teamRequirements = new TeamRequirementsDto(team.getId(), team.getName(), shiftDtos);
        WardRequirementsDto request = new WardRequirementsDto(ward.getId(), ward.getSupervisorId(), List.of(teamRequirements));

        WardRequirementsDto response = requirementsService.updateWardRequirements(request);
        Map<UUID, Map<UUID, Integer>> result = ward.getRequirements();
        Map<UUID, Integer> requirements = result.get(team.getId());
        for (Shift shift : ward.getShifts()) {
            Assertions.assertEquals(2, requirements.get(shift.getId()));
        }

        List<RequirementShiftDto> wardRequirements = response.requirements().getFirst().requirements();
        for (RequirementShiftDto shiftDto : wardRequirements) {
            Assertions.assertEquals(2, shiftDto.requiredCount());
        }
    }
}
