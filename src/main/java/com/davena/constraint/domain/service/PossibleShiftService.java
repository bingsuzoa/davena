package com.davena.constraint.domain.service;

import com.davena.common.WardService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.possibleShifts.MemberPossibleShiftsDto;
import com.davena.constraint.application.dto.possibleShifts.PossibleShiftDto;
import com.davena.constraint.application.dto.possibleShifts.WardPossibleShiftsDto;
import com.davena.constraint.application.dto.possibleShifts.WardPossibleShiftsRequest;
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

    private final WardService wardService;
    private final MemberService memberService;

    public WardPossibleShiftsDto getWardPossibleShifts(WardPossibleShiftsRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, request.supervisorId());
        return getWardPossibleShiftsDto(ward);
    }

    public WardPossibleShiftsDto updateWardPossibleShifts(WardPossibleShiftsDto request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, request.supervisorId());

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
            possibleShiftDtos.add(new PossibleShiftDto(shift.getDayType(), shift.getId(), shift.getName(), possibleShift.isPossible()));
        }
        return possibleShiftDtos;
    }


}
