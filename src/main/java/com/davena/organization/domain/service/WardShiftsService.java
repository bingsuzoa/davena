package com.davena.organization.domain.service;

import com.davena.organization.application.dto.ward.shift.ShiftDto;
import com.davena.organization.application.dto.ward.shift.ShiftRequest;
import com.davena.organization.application.dto.ward.shift.WardShiftsResponse;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.service.util.ExistenceService;
import com.davena.organization.domain.service.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WardShiftsService {

    private final ExistenceService existenceService;
    private final Mapper mapper;

    public WardShiftsResponse getShifts(ShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        return new WardShiftsResponse(ward.getId(), ward.getSupervisorId(), mapper.getShiftsDto(ward.getShifts()));
    }

    public WardShiftsResponse addNewShift(ShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, ward.getSupervisorId());
        ShiftDto shiftDto = request.shiftDto();
        ward.addNewShift(request.dayType(), shiftDto.name(), shiftDto.statTime(), shiftDto.endTime());
        return new WardShiftsResponse(ward.getId(), ward.getSupervisorId(), mapper.getShiftsDto(ward.getShifts()));
    }

    public WardShiftsResponse deleteShift(ShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, ward.getSupervisorId());
        ward.deleteShift(request.dayType(), request.shiftDto().id());
        return new WardShiftsResponse(ward.getId(), ward.getSupervisorId(), mapper.getShiftsDto(ward.getShifts()));
    }

    public WardShiftsResponse updateShift(ShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, ward.getSupervisorId());
        ShiftDto shiftDto = request.shiftDto();
        ward.updateShift(shiftDto.id(), request.dayType(), shiftDto.name(), shiftDto.statTime(), shiftDto.endTime());
        return new WardShiftsResponse(ward.getId(), ward.getSupervisorId(), mapper.getShiftsDto(ward.getShifts()));
    }

}
