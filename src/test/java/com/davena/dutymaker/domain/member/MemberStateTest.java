package com.davena.dutymaker.domain.member;

import com.davena.dutymaker.domain.ShiftType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;

public class MemberStateTest {

    private final ShiftType Day = new ShiftType("day", LocalTime.of(7, 30), LocalTime.of(16, 00), true);
    private final ShiftType Eve = new ShiftType("eve", LocalTime.of(16, 00), LocalTime.of(0, 00), true);
    private final ShiftType Nig = new ShiftType("night", LocalTime.of(0, 0), LocalTime.of(8, 00), true);
    private final ShiftType Off = new ShiftType("off", null, null, false);
    LocalDate now = LocalDate.of(2025, 8, 15);
    Member member = new Member("test");

    @Test
    @DisplayName("오늘 근무 OFF일 때, 연속 나이트 갯수는 0개 확인")
    void 연속_나이트_갯수_0개_확인() {
        ReflectionTestUtils.setField(member, "id", 1L);
        MemberState memberState = new MemberState(1L);
        memberState.updateMemberState(now, Off);
        Assertions.assertEquals(memberState.getConsecNights(), 0);
    }

    @Test
    @DisplayName("오늘 근무 OFF일 때, 어제 근무 여부 false")
    void 어제_근무_여부_false() {
        ReflectionTestUtils.setField(member, "id", 1L);
        MemberState memberState = new MemberState(1L);
        memberState.updateMemberState(now, Off);
        Assertions.assertFalse(memberState.isYesterdayWork());
    }

    @Test
    @DisplayName("오늘 근무 OFF아닐 때, 마지막 근무일은 오늘")
    void 오늘_근무_OFF_아니면_마지막_근무일_오늘() {
        ReflectionTestUtils.setField(member, "id", 1L);
        MemberState memberState = new MemberState(1L);
        memberState.updateMemberState(now, Day);
        Assertions.assertTrue(memberState.isYesterdayWork());
        Assertions.assertEquals(memberState.getLastWorkDate(), now);
        Assertions.assertEquals(memberState.getLastWorkShift(), Day);
    }
}
