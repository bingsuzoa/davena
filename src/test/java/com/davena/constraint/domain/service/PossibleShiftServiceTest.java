package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.possibleShifts.MemberPossibleShiftsDto;
import com.davena.constraint.application.dto.possibleShifts.PossibleShiftDto;
import com.davena.constraint.application.dto.possibleShifts.WardPossibleShiftsDto;
import com.davena.constraint.application.dto.possibleShifts.WardPossibleShiftsRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.PossibleShift;
import com.davena.organization.application.dto.ward.shift.ShiftDto;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.DayType;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PossibleShiftServiceTest {

    @Mock
    private ExistenceService existenceService;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private PossibleShiftService possibleShiftService;

    @Test
    @DisplayName("병동 내 멤버들의 가능한 근무 목록 조회하기 - 처음에는 모든 근무가 (가능)으로 설정됨")
    void getWardPossibleShifts() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());

        User user2 = User.create("name1", "loginId1", "password", "01011112222");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.initPossibleShifts(ward.getShifts());

        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);
        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2));

        WardPossibleShiftsDto response = possibleShiftService.getWardPossibleShifts(new WardPossibleShiftsRequest(ward.getId(), ward.getSupervisorId()));

        List<MemberPossibleShiftsDto> allMemberShifts = response.shits();
        Assertions.assertEquals(2, allMemberShifts.size());

        List<PossibleShiftDto> shiftDto = allMemberShifts.getFirst().shifts();
        Assertions.assertEquals(8, shiftDto.size());
        for(PossibleShiftDto shift : shiftDto) {
            Assertions.assertEquals(true, shift.isPossible());
        }
    }

    @Test
    @DisplayName("병동 내 멤버들의 가능한 근무 변경하기")
    void updateWardPossibleShifts() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());
        when(memberService.getMember(member1.getUserId())).thenReturn(member1);

        User user2 = User.create("name1", "loginId1", "password", "01011112222");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.initPossibleShifts(ward.getShifts());
        when(memberService.getMember(member2.getUserId())).thenReturn(member2);

        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);
        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2));

        List<MemberPossibleShiftsDto> allMembersShifts = new ArrayList<>();
        List<PossibleShiftDto> member1PossibleShifts = new ArrayList<>();
        for(Shift shift : ward.getShifts()) {
            member1PossibleShifts.add(new PossibleShiftDto(shift.getDayType(), shift.getId(), shift.getName(), false));
        }
        allMembersShifts.add(new MemberPossibleShiftsDto(member1.getUserId(), member1.getName(), member1PossibleShifts));

        List<PossibleShiftDto> member2PossibleShifts = new ArrayList<>();
        for(Shift shift : ward.getShifts()) {
            member2PossibleShifts.add(new PossibleShiftDto(shift.getDayType(), shift.getId(), shift.getName(), true));
        }
        allMembersShifts.add(new MemberPossibleShiftsDto(member2.getUserId(), member2.getName(), member2PossibleShifts));
        possibleShiftService.updateWardPossibleShifts(new WardPossibleShiftsDto(ward.getId(), ward.getSupervisorId(), allMembersShifts));

        List<PossibleShift> shifts = member1.getShifts();
        for(PossibleShift possibleShift : shifts) {
            Assertions.assertEquals(possibleShift.isPossible(), false);
        }
    }
}
