package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.holidayRequest.*;
import com.davena.constraint.domain.model.HolidayRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.port.HolidayRepository;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.davena.constraint.domain.service.HolidayService.ALREADY_EXIST_HOLIDAY_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {

    @Mock
    private ExistenceService existenceService;
    @Mock
    private MemberService memberService;
    @Mock
    private HolidayRepository holidayRepository;
    @InjectMocks
    private HolidayService holidayService;

    /// ///해피 테스트

    @Test
    @DisplayName("병동 근무자 휴가 신청 현황 조회")
    void getWardHolidays() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        User user2 = User.create("name1", "loginId1", "password", "01011112222");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        when(memberService.getMember(user2.getId())).thenReturn(member2);

        HolidayRequest member1Req1 = new HolidayRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), "");
        HolidayRequest member2Req1 = new HolidayRequest(UUID.randomUUID(), member2.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), "");
        HolidayRequest member2Req2 = new HolidayRequest(UUID.randomUUID(), member2.getUserId(), 2025, 9, LocalDate.of(2025, 9, 21), "");

        when(existenceService.getWard(any())).thenReturn(ward);
        when(existenceService.verifySupervisor(any(), any())).thenReturn(true);
        when(holidayRepository.findByWardIdAndYearAndMonth(any(), anyInt(), anyInt())).thenReturn(List.of(member1Req1, member2Req1, member2Req2));

        WardHolidayResponse response = holidayService.getWardHolidays(new WardHolidayRequest(ward.getId(), ward.getSupervisorId(), 2025, 9));
        Map<UUID, MemberHolidayResponse> membersReq = response.requests();
        Assertions.assertEquals(1, membersReq.get(member1.getUserId()).requests().size());
        Assertions.assertEquals(2, membersReq.get(member2.getUserId()).requests().size());
    }

    @Test
    @DisplayName("휴가 신청하는 기능")
    void addMemberHoliday() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        when(existenceService.getWard(any())).thenReturn(ward);
        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        when(holidayRepository.findByMemberIdAndRequestDay(any(), any(), any())).thenReturn(Optional.empty());
        HolidayRequest member1Req1 = new HolidayRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), "");
        when(holidayRepository.save(any())).thenReturn(member1Req1);
        when(holidayRepository.findByMemberIdAndYearAndMonth(any(), anyInt(), anyInt())).thenReturn(List.of(member1Req1));

        MemberHolidayResponse response = holidayService.addMemberHoliday(new CreateHolidayRequest(ward.getId(), member1.getUserId(), LocalDate.of(2025, 9, 20), ""));
        Assertions.assertEquals(1, response.requests().size());
    }

    @Test
    @DisplayName("휴가 삭제하는 기능")
    void deleteMemberHoliday() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        when(existenceService.getWard(any())).thenReturn(ward);
        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        HolidayRequest member1Req1 = new HolidayRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), "");
        when(holidayRepository.findByMemberIdAndRequestDay(any(), any(), any())).thenReturn(Optional.of(member1Req1));

        MemberHolidayResponse response = holidayService.deleteMemberHoliday(new DeleteHolidayRequest(ward.getId(), member1.getUserId(), LocalDate.of(20205, 9, 20)));
        Assertions.assertEquals(0, response.requests().size());
    }

    /// ///예외 테스트
    @Test
    @DisplayName("휴가 신청하는 기능 - 같은 날짜 휴가 신청 내역 있으면 예외")
    void addMemberHoliday_같은_날짜_휴가_신청_내역_있으면_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        when(existenceService.getWard(any())).thenReturn(ward);
        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        HolidayRequest member1Req1 = new HolidayRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), "");
        when(holidayRepository.findByMemberIdAndRequestDay(any(), any(), any())).thenReturn(Optional.of(member1Req1));

        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> holidayService.addMemberHoliday(new CreateHolidayRequest(ward.getId(), member1.getUserId(), LocalDate.of(2025, 9, 20), ""))
        );
        Assertions.assertEquals(ALREADY_EXIST_HOLIDAY_REQUEST, e.getMessage());
    }

    @Test
    @DisplayName("휴가 삭제하는 기능 - 휴가 삭제하려는 날짜에 신청된 내역 없으면 예외 발생")
    void deleteMemberHoliday_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());

        when(existenceService.getWard(any())).thenReturn(ward);
        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        HolidayRequest member1Req1 = new HolidayRequest(UUID.randomUUID(), member1.getUserId(), 2025, 9, LocalDate.of(2025, 9, 20), "");
        when(holidayRepository.findByMemberIdAndRequestDay(any(), any(), any())).thenReturn(Optional.empty());

        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> holidayService.deleteMemberHoliday(new DeleteHolidayRequest(ward.getId(), member1.getUserId(), LocalDate.of(20205, 9, 20)))
        );
    }
}
