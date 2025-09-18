package com.davena.organization.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.PossibleShift;
import com.davena.organization.application.dto.ward.shift.*;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.DayType;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.service.WardShiftsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.davena.organization.domain.model.ward.Ward.ALREADY_EXIST_SHIFT_NAME;
import static com.davena.organization.domain.service.WardShiftsService.CAN_NOT_DELETE_OFF;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WardShiftsServiceTest {

    @Mock
    private ExistenceService existenceService;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private WardShiftsService wardShiftsService;

    /// ///해피 테스트

    @Test
    @DisplayName("병동의 근무 유형 조회")
    void getShifts() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        WardShiftsDto response = wardShiftsService.getShifts(new GetShiftRequest(ward.getId(), ward.getSupervisorId()));
        Assertions.assertEquals(8, response.shifts().size());
    }

    @Test
    @DisplayName("새로운 근무 유형 추가")
    void addNewShift() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());

        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1));
        WardShiftsDto response = wardShiftsService.addNewShift(new CreateShiftRequest(ward.getId(), ward.getSupervisorId(), DayType.WEEKDAY, "eE", 14, 0, 22, 0));

        List<PossibleShift> shifts = member1.getShifts();
        Assertions.assertEquals(9, shifts.size());
        Assertions.assertEquals(9, response.shifts().size());
    }

    @Test
    @DisplayName("기존 근무 유형 삭제")
    void deleteShift() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());

        Shift shift = ward.getShifts().getFirst();
        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1));
        WardShiftsDto response = wardShiftsService.deleteShift(new DeleteShiftRequest(ward.getId(), ward.getSupervisorId(), shift.getId()));

        List<PossibleShift> shifts = member1.getShifts();
        Assertions.assertEquals(7, shifts.size());
        Assertions.assertEquals(7, response.shifts().size());
    }

    @Test
    @DisplayName("근무 유형 변경하기")
    void updateShift() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());

        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1));

        List<Shift> wardShifts = ward.getShifts();
        ShiftDto shift1 = new ShiftDto(wardShifts.get(0).getId(), DayType.WEEKDAY, Shift.Day, false, 7, 30, 16, 30);
        ShiftDto shift2 = new ShiftDto(wardShifts.get(1).getId(), DayType.WEEKDAY, Shift.Eve, false, 16, 0, 0, 0);
        ShiftDto shift3 = new ShiftDto(wardShifts.get(2).getId(), DayType.WEEKDAY, Shift.Nig, false, 0, 0, 8, 0);
        ShiftDto shift4 = new ShiftDto(wardShifts.get(3).getId(), DayType.WEEKDAY, Shift.Off, true, null, null, null, null);
        ShiftDto shift5 = new ShiftDto(wardShifts.get(4).getId(), DayType.WEEKEND, Shift.Day, false, 8, 0, 16, 0);
        ShiftDto shift6 = new ShiftDto(wardShifts.get(5).getId(), DayType.WEEKEND, Shift.Eve, false, 16, 0, 0, 0);
        ShiftDto shift7 = new ShiftDto(wardShifts.get(6).getId(), DayType.WEEKEND, Shift.Nig, false, 0, 0, 8, 0);
        ShiftDto shift8 = new ShiftDto(wardShifts.get(7).getId(), DayType.WEEKEND, Shift.Off, true, null, null, null, null);

        WardShiftsDto wardShiftsDto = new WardShiftsDto(ward.getId(), ward.getSupervisorId(), List.of(shift1, shift2, shift3, shift4, shift5, shift6, shift7, shift8));
        wardShiftsService.updateShift(wardShiftsDto);
        Shift updateShift = ward.getShift(shift1.id());
        Assertions.assertEquals(updateShift.getEndTime(), LocalTime.of(16, 30));
        Assertions.assertEquals(30, wardShiftsDto.shifts().getFirst().endMinute());
    }


    /// ///예외 테스트
    @Test
    @DisplayName("새로운 근무 유형 추가시 기존 근무 유형의 이름과 동일할 경우 예외 발생")
    void addNewShift_동일_이름_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());

        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> wardShiftsService.addNewShift(new CreateShiftRequest(ward.getId(), ward.getSupervisorId(), DayType.WEEKDAY, Shift.Day, 14, 0, 22, 0))
        );
        Assertions.assertEquals(ALREADY_EXIST_SHIFT_NAME, e.getMessage());
    }

    @Test
    @DisplayName("기존 근무 유형 삭제 - 오프 삭제 시도 시 예외")
    void deleteShift_오프_삭제_시_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());

        List<Shift> shifts = ward.getShifts();
        Shift off = null;
        for (Shift shift : shifts) {
            if (shift.isOff()) {
                off = shift;
                break;
            }
        }
        UUID offId = off.getId();
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> wardShiftsService.deleteShift(new DeleteShiftRequest(ward.getId(), ward.getSupervisorId(), offId))
        );
        Assertions.assertEquals(CAN_NOT_DELETE_OFF, e.getMessage());
    }
}