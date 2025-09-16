package com.davena.constraint.domain.service;

import com.davena.organization.domain.model.ward.DayType;
import com.davena.constraint.application.dto.possibleShifts.AllMembersPossibleShiftsDto;
import com.davena.constraint.application.dto.possibleShifts.AllMembersPossibleShiftsRequest;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.common.ExistenceService;
import com.davena.constraint.application.dto.possibleShifts.MemberPossibleShiftsDto;
import com.davena.constraint.application.dto.possibleShifts.PossibleShiftDto;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.PossibleShift;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PossibleShiftService {

    private final ExistenceService existenceService;

    public AllMembersPossibleShiftsDto getAllMembersPossibleShifts(AllMembersPossibleShiftsRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        return getAllMembersPossibleShiftsDto(existenceService.getAllMembersOfWard(ward.getId()), ward);
    }

    public AllMembersPossibleShiftsDto updateAllMembersPossibleShifts(AllMembersPossibleShiftsDto request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        for(MemberPossibleShiftsDto memberPossibleDto : request.membersPossibleShiftsDto()) {
            Member member = existenceService.getMember(memberPossibleDto.userId());
            updateMemberPossibleShift(member, memberPossibleDto.memberPossibleShifts());
        }
        return getAllMembersPossibleShiftsDto(existenceService.getAllMembersOfWard(ward.getId()), ward);
    }

    private void updateMemberPossibleShift(Member member, Map<DayType, List<PossibleShiftDto>> possibleShifts) {
        for(DayType dayType : possibleShifts.keySet()) {
            for(PossibleShiftDto possibleShift : possibleShifts.get(dayType)) {
                member.updateIsPossibleOfShift(dayType, possibleShift.shiftId(), possibleShift.isPossible());
            }
        }
    }

    private AllMembersPossibleShiftsDto getAllMembersPossibleShiftsDto(List<Member> allMembers, Ward ward) {
        List<MemberPossibleShiftsDto> memberPossibleShiftsDto = new ArrayList<>();
        for(Member member : allMembers) {
            memberPossibleShiftsDto.add(getMemberPossibleShiftsDto(member));
        }
        return new AllMembersPossibleShiftsDto(ward.getId(), ward.getSupervisorId(), memberPossibleShiftsDto);
    }

    private MemberPossibleShiftsDto getMemberPossibleShiftsDto(Member member) {
        Map<DayType, List<PossibleShiftDto>> possibleShiftsOfMemberDto = new HashMap<>();
        Map<DayType, List<PossibleShift>> possibleShiftsOfMember = member.getPossibleShifts();

        for(DayType dayType : possibleShiftsOfMember.keySet()) {
            possibleShiftsOfMemberDto.put(dayType, new ArrayList<>());
            for(PossibleShift shift : possibleShiftsOfMember.get(dayType)) {
                possibleShiftsOfMemberDto.get(dayType).add(new PossibleShiftDto(shift.getShiftId(),shift.getName(), shift.isPossible()));
            }
        }
        return new MemberPossibleShiftsDto(member.getUserId(), member.getName(), possibleShiftsOfMemberDto);
    }
}
