package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.shiftRequest.*;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.UnavailShiftRequest;
import com.davena.constraint.domain.port.UnavailShiftRepository;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.davena.constraint.domain.service.UnavailShiftService.ALREADY_EXIST_SHIFT_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnavailShiftServiceTest {

    @Mock
    private UnavailShiftRepository unavailShiftRepository;
    @Mock
    private ExistenceService existenceService;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private UnavailShiftService unavailShiftService;

    /// ///해피 테스트

    @Test
    @DisplayName("병동 멤버 전체의 불가능 근무 리퀘스트 조회하기")
    void getWardUnavailShiftRequests() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());
        when(memberService.getMember(member1.getUserId())).thenReturn(member1);

        List<Shift> wardShifts = ward.getShifts();
        Shift shift1 = wardShifts.get(0);

        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);

        UnavailShiftRequest req1 = new UnavailShiftRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), shift1.getId(), "");
        when(unavailShiftRepository.findByWardIdAndYearAndMonth(any(), anyInt(), anyInt())).thenReturn(List.of(req1));
        WardUnavailShiftRequest request = new WardUnavailShiftRequest(ward.getId(), ward.getSupervisorId(), 2025, 9);
        WardUnavailShiftResponse response = unavailShiftService.getWardUnavailShiftRequests(request);
        Assertions.assertEquals(1, response.unavailShifts().size());
    }

    @Test
    @DisplayName("불가능 근무 신청하기")
    void addMemberUnavailShift() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());
        when(memberService.getMember(member1.getUserId())).thenReturn(member1);

        List<Shift> wardShifts = ward.getShifts();
        Shift shift1 = wardShifts.get(0);

        when(existenceService.getWard(any())).thenReturn(ward);

        when(unavailShiftRepository.findByMemberIdAndShiftIdAndRequestDay(any(), any(), any())).thenReturn(Optional.empty());
        UnavailShiftRequest req1 = new UnavailShiftRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), shift1.getId(), "");
        when(unavailShiftRepository.save(any())).thenReturn(req1);
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(any(), anyInt(), anyInt())).thenReturn(List.of(req1));

        MemberUnavailShiftsResponse response = unavailShiftService.addMemberUnavailShift(new CreateShiftRequest(ward.getId(), member1.getUserId(), shift1.getId(), LocalDate.of(2025, 9, 20), ""));
        Assertions.assertEquals(1, response.unavailableShifts().size());
    }

    @Test
    @DisplayName("불가능 근무 삭제하기")
    void deleteMemberUnavailShift() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());
        when(memberService.getMember(member1.getUserId())).thenReturn(member1);

        List<Shift> wardShifts = ward.getShifts();
        Shift shift1 = wardShifts.get(0);

        when(existenceService.getWard(any())).thenReturn(ward);

        UnavailShiftRequest req1 = new UnavailShiftRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), shift1.getId(), "");
        when(unavailShiftRepository.findByMemberIdAndShiftIdAndRequestDay(any(), any(), any())).thenReturn(Optional.of(req1));
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(any(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        MemberUnavailShiftsResponse response = unavailShiftService.deleteMemberUnavailShift(new DeleteShiftRequest(ward.getId(), member1.getUserId(), shift1.getId(), LocalDate.of(2025, 9, 20)));
        Assertions.assertEquals(0, response.unavailableShifts().size());
    }


    /// /// 예외 테스트
    @Test
    @DisplayName("불가능 근무 신청하기 - 동일 날짜 동일 근무 신청 내역 있으면 예외")
    void addMemberUnavailShift_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.initPossibleShifts(ward.getShifts());
        when(memberService.getMember(member1.getUserId())).thenReturn(member1);

        List<Shift> wardShifts = ward.getShifts();
        Shift shift1 = wardShifts.get(0);

        when(existenceService.getWard(any())).thenReturn(ward);

        UnavailShiftRequest req1 = new UnavailShiftRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), shift1.getId(), "");
        when(unavailShiftRepository.findByMemberIdAndShiftIdAndRequestDay(any(), any(), any())).thenReturn(Optional.of(req1));
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> unavailShiftService.addMemberUnavailShift(new CreateShiftRequest(ward.getId(), member1.getUserId(), shift1.getId(), LocalDate.of(2025, 9, 20), ""))
        );
        Assertions.assertEquals(ALREADY_EXIST_SHIFT_REQUEST, e.getMessage());
    }
}
