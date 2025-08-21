package com.davena.dutymaker.domain.organization.member;

//public class MemberStateTest {
//
//    private final ShiftType Day = new ShiftType("day", LocalTime.of(7, 30), LocalTime.of(16, 00), true);
//    private final ShiftType Off = new ShiftType("off", null, null, false);
//    LocalDate now = LocalDate.of(2025, 8, 15);
//    Hospital hospital = new Hospital();
//    Ward ward = new Ward(hospital, "A병동");
//    Team team1 = new Team(ward, "A팀");
//    Member member = new Member("test");
//
//    @Test
//    @DisplayName("오늘 근무 OFF일 때, 연속 나이트 갯수는 0개 확인")
//    void 연속_나이트_갯수_0개_확인() {
//        ReflectionTestUtils.setField(member, "id", 1L);
//        MemberState memberState = new MemberState(1L, team1.getName());
//        memberState.updateMemberState(now, Off);
//        Assertions.assertEquals(memberState.getConsecNights(), 0);
//    }
//
//    @Test
//    @DisplayName("오늘 근무 OFF일 때, 어제 근무 여부 false")
//    void 어제_근무_여부_false() {
//        ReflectionTestUtils.setField(member, "id", 1L);
//        MemberState memberState = new MemberState(1L, team1.getName());
//        memberState.updateMemberState(now, Off);
//        Assertions.assertFalse(memberState.isYesterdayWork());
//    }
//
//    @Test
//    @DisplayName("오늘 근무 OFF아닐 때, 마지막 근무일은 오늘")
//    void 오늘_근무_OFF_아니면_마지막_근무일_오늘() {
//        ReflectionTestUtils.setField(member, "id", 1L);
//        MemberState memberState = new MemberState(1L, team1.getName());
//        memberState.updateMemberState(now, Day);
//        Assertions.assertTrue(memberState.isYesterdayWork());
//        Assertions.assertEquals(memberState.getLastWorkDate(), now);
//        Assertions.assertEquals(memberState.getLastWorkShift(), Day);
//    }
//}
