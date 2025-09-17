package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.possibleShifts.*;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.PossibleShift;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PossibleShiftService {

    private final ExistenceService existenceService;
    private final MemberService memberService;

    public WardPossibleShiftsDto getWardPossibleShifts(WardPossibleShiftsRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        return getWardPossibleShiftsDto(ward);
    }

    public WardPossibleShiftsDto updateWardPossibleShifts(WardPossibleShiftsDto request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());

        for (MemberPossibleShiftsDto possibleShiftsDto : request.shits()) {
            updateMemberShiftIsPossible(possibleShiftsDto.userId(), possibleShiftsDto.shifts());
        }
        return getWardPossibleShiftsDto(ward);
    }

    private void updateMemberShiftIsPossible(UUID memberId, List<PossibleShiftDto> memberPossibleShifts) {
        Member member = memberService.getMember(memberId);
        for (PossibleShiftDto shiftDto : memberPossibleShifts) {
            member.updateIsPossibleOfShift(shiftDto.shiftId(), shiftDto.isPossible());
        }
    }

    public MemberPossibleShiftsDto getMemberPossibleShifts(MemberPossibleShiftsRequest request) {
        Member member = memberService.getMember(request.memberId());
        Ward ward = existenceService.getWard(member.getWardId());
        return new MemberPossibleShiftsDto(member.getUserId(), member.getName(), getMemberPossibleShiftsDto(member, ward));
    }

    private WardPossibleShiftsDto getWardPossibleShiftsDto(Ward ward) {
        List<Member> allMembers = memberService.getAllMembersOfWard(ward.getId());
        List<MemberPossibleShiftsDto> memberPossibleShiftsDto = new ArrayList<>();

        for (Member member : allMembers) {
            memberPossibleShiftsDto.add(new MemberPossibleShiftsDto(member.getUserId(), member.getName(), getMemberPossibleShiftsDto(member, ward)));
        }
        return new WardPossibleShiftsDto(ward.getId(), ward.getSupervisorId(), memberPossibleShiftsDto);
    }

    private List<PossibleShiftDto> getMemberPossibleShiftsDto(Member member, Ward ward) {
        List<PossibleShift> possibleShifts = member.getShifts();

        List<PossibleShiftDto> possibleShiftDtos = new ArrayList<>();
        for (PossibleShift possibleShift : possibleShifts) {
            Shift shift = ward.getShift(possibleShift.getShiftId());
            possibleShiftDtos.add(new PossibleShiftDto(shift.getDayType(), shift.getId(), shift.getName(), shift.isOff()));
        }
        return possibleShiftDtos;
    }


}
