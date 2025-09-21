package com.davena.organization.domain.service;

import com.davena.common.WardService;
import com.davena.common.MemberService;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.application.dto.ward.shift.*;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WardShiftsService {

    private final WardService wardService;
    private final MemberService memberService;

    public static final String CAN_NOT_DELETE_OFF = "오프는 삭제할 수 없습니다.";

    public WardShiftsDto getShifts(GetShiftRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, request.supervisorId());
        return getWardShiftsDto(ward);
    }

    public WardShiftsDto addNewShift(CreateShiftRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, ward.getSupervisorId());
        UUID newShiftId = ward.addNewShift(request.dayType(), request.shiftName(), request.startHour(), request.startMinute(), request.endHour(), request.endMinute());
        addMembersNewShift(ward, newShiftId, request.shiftName());
        return getWardShiftsDto(ward);
    }

    private void addMembersNewShift(Ward ward, UUID shiftId, String shiftName) {
        List<Member> allMembers = memberService.getAllMembersOfWard(ward.getId());
        for (Member member : allMembers) {
            member.addWardNewShift(shiftId, shiftName);
        }
    }

    public WardShiftsDto deleteShift(DeleteShiftRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, ward.getSupervisorId());
        UUID deletedShiftId = ward.deleteShift(request.shiftId());
        deleteMembersShift(deletedShiftId, ward);
        return getWardShiftsDto(ward);
    }

    private void deleteMembersShift(UUID shiftId, Ward ward) {
        List<Member> allMembers = memberService.getAllMembersOfWard(ward.getId());
        for (Member member : allMembers) {
            member.deleteWardShift(shiftId);
        }
    }

    public WardShiftsDto updateShift(WardShiftsDto request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, ward.getSupervisorId());
        List<ShiftDto> shiftDtos = request.shifts();
        for (ShiftDto shiftDto : shiftDtos) {
            ward.updateShift(shiftDto.id(), shiftDto.dayType(), shiftDto.isOff(), shiftDto.name(), shiftDto.startHour(), shiftDto.startMinute(), shiftDto.endHour(), shiftDto.endMinute());
            updateMembersShift(shiftDto.id(), shiftDto.name(), ward);
        }
        return getWardShiftsDto(ward);
    }

    private void updateMembersShift(UUID shiftId, String shiftName, Ward ward) {
        List<Member> allMembers = memberService.getAllMembersOfWard(ward.getId());
        for (Member member : allMembers) {
            member.updateWardShifts(shiftId, shiftName);
        }
    }

    private WardShiftsDto getWardShiftsDto(Ward ward) {
        List<Shift> shifts = ward.getShifts();
        List<ShiftDto> shiftDtos = new ArrayList<>();

        for (Shift shift : shifts) {
            if (shift.isOff()) {
                shiftDtos.add(new ShiftDto(shift.getId(), shift.getDayType(), shift.getName(), shift.isOff(), null, null, null, null));
            } else {
                LocalTime start = shift.getStartTime();
                LocalTime end = shift.getEndTime();
                shiftDtos.add(new ShiftDto(shift.getId(), shift.getDayType(), shift.getName(), shift.isOff(), start.getHour(), start.getMinute(), end.getHour(), end.getMinute()));
            }
        }
        return new WardShiftsDto(ward.getId(), ward.getSupervisorId(), shiftDtos);
    }


}
