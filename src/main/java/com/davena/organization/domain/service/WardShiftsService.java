package com.davena.organization.domain.service;

import com.davena.common.ExistenceService;
import com.davena.organization.application.dto.ward.shift.ShiftDto;
import com.davena.organization.application.dto.ward.shift.ShiftRequest;
import com.davena.organization.application.dto.ward.shift.WardShiftsResponse;
import com.davena.organization.domain.model.ward.DayType;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.service.util.Mapper;
import com.davena.constraint.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

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
        UUID newShiftId = ward.addNewShift(request.dayType(), shiftDto.name(), shiftDto.statTime(), shiftDto.endTime());
        addMembersNewShift(request.dayType(), newShiftId, shiftDto.name(), ward);
        return new WardShiftsResponse(ward.getId(), ward.getSupervisorId(), mapper.getShiftsDto(ward.getShifts()));
    }

    private void addMembersNewShift(DayType dayType, UUID shiftId, String shiftName, Ward ward) {
        Set<UUID> userIds = ward.getUsers();
        for (UUID userId : userIds) {
            Member member = existenceService.getMember(userId);
            member.addWardNewShift(dayType, shiftId, shiftName);
        }
    }

    public WardShiftsResponse deleteShift(ShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, ward.getSupervisorId());
        UUID deletedShiftId = ward.deleteShift(request.dayType(), request.shiftDto().id());
        deleteMembersShift(request.dayType(), deletedShiftId, ward);
        return new WardShiftsResponse(ward.getId(), ward.getSupervisorId(), mapper.getShiftsDto(ward.getShifts()));
    }

    private void deleteMembersShift(DayType dayType, UUID shiftId, Ward ward) {
        Set<UUID> userIds = ward.getUsers();
        for (UUID userId : userIds) {
            Member member = existenceService.getMember(userId);
            member.deleteWardShift(dayType, shiftId);
        }
    }

    public WardShiftsResponse updateShift(ShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, ward.getSupervisorId());
        ShiftDto shiftDto = request.shiftDto();
        UUID shiftId = ward.updateShift(shiftDto.id(), request.dayType(), shiftDto.name(), shiftDto.statTime(), shiftDto.endTime());
        updateMembersNewShift(request.dayType(), shiftId, shiftDto.name(), ward);
        return new WardShiftsResponse(ward.getId(), ward.getSupervisorId(), mapper.getShiftsDto(ward.getShifts()));
    }

    private void updateMembersNewShift(DayType dayType, UUID shiftId, String shiftName, Ward ward) {
        Set<UUID> userIds = ward.getUsers();
        for (UUID userId : userIds) {
            Member member = existenceService.getMember(userId);
            member.addWardNewShift(dayType, shiftId, shiftName);
        }
    }


}
