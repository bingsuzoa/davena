package com.davena.dutymaker.domain.schedule;

import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ScheduleTest {

    @Test
    @DisplayName("한 병동에 월 Schedule 객체는 한 개만 가능")
    void schedule_객체_확인() {
        Hospital hospital = new Hospital();
        Member supervisor = new Member("김간호", "김간호", "01011112222", "1234");
        ;
        Ward ward = new Ward(hospital, supervisor, "외상 병동");

        Schedule schedule = new Schedule(ward, "2025-09");
    }
}
