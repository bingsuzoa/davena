package com.davena.dutymaker.service;

import com.davena.dutymaker.domain.Assignment;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.domain.organization.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

//public class HardPolicyFilterTest {
//
//    private final ShiftType Day = new ShiftType("day", LocalTime.of(7, 30), LocalTime.of(16, 00), true);
//    private final ShiftType Eve = new ShiftType("eve", LocalTime.of(16, 00), LocalTime.of(0, 00), true);
//    private final ShiftType Nig = new ShiftType("night", LocalTime.of(0, 0), LocalTime.of(8, 00), true);
//    private final ShiftType Off = new ShiftType("off", null, null, false);
//    LocalDate now = LocalDate.of(2025, 8, 15);
//    Member member = new Member("test");
//    HardPolicyFilter hardPolicyFilter = new HardPolicyFilter();
//
//    List<Assignment> assignments = new ArrayList<>();
//    Assignment day1 = new Assignment(member, now.minusDays(7), Nig);
//    Assignment day2 = new Assignment(member, now.minusDays(6), Nig);
//    Assignment day3 = new Assignment(member, now.minusDays(5), Day);
//    Assignment day4 = new Assignment(member, now.minusDays(4), Day);
//    Assignment day5 = new Assignment(member, now.minusDays(3), Day);
//    Assignment day6 = new Assignment(member, now.minusDays(2), Nig);
//    Assignment yesterday = new Assignment(member, now.minusDays(1), Nig);
//
//    @BeforeEach
//    void setAssignments() {
//        ReflectionTestUtils.setField(member, "id", 1L);
//        assignments.add(day1);
//        assignments.add(day2);
//        assignments.add(day3);
//        assignments.add(day4);
//        assignments.add(day5);
//        assignments.add(day6);
//        assignments.add(yesterday);
//    }
//
//}
