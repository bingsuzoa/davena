package com.davena.dutymaker.service.generator;


import com.davena.dutymaker.api.dto.schedule.payload.draft.Draft;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftCell;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.api.dto.schedule.requirement.RequirementRequest;
import com.davena.dutymaker.api.dto.ward.WardRequest;
import com.davena.dutymaker.domain.Request;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.*;
import com.davena.dutymaker.service.DraftService;
import com.davena.dutymaker.service.ScheduleService;
import com.davena.dutymaker.service.ShiftTypeService;
import com.davena.dutymaker.service.WardService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@DataJpaTest
@Import({
        ScheduleService.class,
        PreCheck.class,
        DraftService.class,
        WardService.class,
        ShiftTypeService.class
})
@ActiveProfiles("test")
public class PreCheckIntegrationTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ShiftTypeRepository shiftRepository;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private SkillGradeRepository gradeRepository;
    @Autowired
    private DraftService draftService;
    @Autowired
    private RequirementRuleRepository requirementRuleRepository;
    @Autowired
    private DraftRepository draftRepository;
    @Autowired
    private PreCheck preCheck;
    @Autowired
    private WardService wardService;
    @Autowired
    private ShiftTypeService service;

    Ward getWard() {
        Hospital hospital = hospitalRepository.save(new Hospital());
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        return wardService.createWardAndOffType(supervisor.getId(), new WardRequest(hospital.getId(), "외상 병동"));
    }

    void init(Ward ward) {
        Team aTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();
        SkillGrade firstGrade = gradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();

        Team bTeam = teamRepository.save(new Team(ward, "b팀"));

        ShiftType D = shiftRepository.save(new ShiftType(ward, "Day", LocalTime.of(7, 30), LocalTime.of(16, 30), true));
        ShiftType E = shiftRepository.save(new ShiftType(ward, "Eve", LocalTime.of(16, 00), LocalTime.of(0, 0), true));
        ShiftType N = shiftRepository.save(new ShiftType(ward, "Nig", LocalTime.of(0, 0), LocalTime.of(8, 0), true));

        Map<DayType, Map<ShiftType, Integer>> aTeamRule = new HashMap<>();
        Map<ShiftType, Integer> aTeamWeekDayRequired = new HashMap<>();
        aTeamWeekDayRequired.put(D, 2);
        aTeamWeekDayRequired.put(E, 1);
        aTeamWeekDayRequired.put(N, 1);
        aTeamRule.put(DayType.WEEKDAY, aTeamWeekDayRequired);
        Map<ShiftType, Integer> aTeamWeekendRequired = new HashMap<>();
        aTeamWeekendRequired.put(D, 1);
        aTeamWeekendRequired.put(E, 1);
        aTeamWeekendRequired.put(N, 1);
        aTeamRule.put(DayType.WEEKEND, aTeamWeekendRequired);
        scheduleService.updateRule(ward.getId(), new RequirementRequest(aTeam.getId(), aTeamRule));

        Map<DayType, Map<ShiftType, Integer>> bTeamRule = new HashMap<>();
        Map<ShiftType, Integer> bTeamWeekDayRequired = new HashMap<>();
        bTeamWeekDayRequired.put(D, 2);
        bTeamWeekDayRequired.put(E, 1);
        bTeamWeekDayRequired.put(N, 1);
        bTeamRule.put(DayType.WEEKDAY, bTeamWeekDayRequired);
        Map<ShiftType, Integer> bTeamWeekendRequired = new HashMap<>();
        bTeamWeekendRequired.put(D, 1);
        bTeamWeekendRequired.put(E, 1);
        bTeamWeekendRequired.put(N, 1);
        bTeamRule.put(DayType.WEEKEND, bTeamWeekendRequired);
        scheduleService.updateRule(ward.getId(), new RequirementRequest(bTeam.getId(), bTeamRule));


        Member member1 = new Member("박간호", "박간호", "01011112223", "1234");
        member1.isCharge(true, 1);
        memberRepository.save(member1);
        member1.joinWard(ward, aTeam, firstGrade);

        Member member2 = new Member("김가영", "김가영", "01011112224", "1234");
        member2.isCharge(true, 1);
        memberRepository.save(member2);
        member2.joinWard(ward, aTeam, firstGrade);

        Member member3 = new Member("김나영", "김나영", "01011112225", "1234");
        member3.isCharge(true, 1);
        memberRepository.save(member3);
        member3.joinWard(ward, aTeam, firstGrade);

        Member member4 = new Member("김다영", "김다영", "01011112226", "1234");
        member4.isCharge(true, 1);
        memberRepository.save(member4);
        member4.joinWard(ward, aTeam, firstGrade);

        Member member5 = new Member("김라영", "김라영", "01011112227", "1234");
        memberRepository.save(member5);
        member5.joinWard(ward, aTeam, firstGrade);

        Member member6 = new Member("김마영", "김마영", "01011112228", "1234");
        memberRepository.save(member6);
        member6.joinWard(ward, aTeam, firstGrade);

        Member member7 = new Member("김바영", "김바영", "01011112229", "1234");
        memberRepository.save(member7);
        member7.joinWard(ward, bTeam, firstGrade);

        Member member8 = new Member("김사영", "김사영", "01011112210", "1234");
        memberRepository.save(member8);
        member8.joinWard(ward, bTeam, firstGrade);

        Member member9 = new Member("김아영", "김아영", "01011112211", "1234");
        memberRepository.save(member9);
        member9.joinWard(ward, bTeam, firstGrade);

        Member member10 = new Member("김자영", "김자영", "01011112212", "1234");
        member10.isCharge(true, 1);
        memberRepository.save(member10);
        member10.joinWard(ward, bTeam, firstGrade);

        Member member11 = new Member("김차영", "김차영", "01011112213", "1234");
        member11.isCharge(true, 1);
        memberRepository.save(member11);
        member11.joinWard(ward, bTeam, firstGrade);

        Member member12 = new Member("김다영", "김다영", "01011112214", "1234");
        memberRepository.save(member12);
        member12.joinWard(ward, bTeam, firstGrade);
    }

    /// ///해피 테스트
    @Test
    @DisplayName("근무표 자동 생성할 수 있는 최소 조건 충족 확인: 전체 팀 인원 6명, 평일 기준 하루에 4명이 필요, 필요량 보다 휴가 더 많이 신청 시 예외")
    void 평일_기준_휴가_신청_통과() {
        Ward ward = getWard();
        init(ward);
        Member member1 = memberRepository.findByPhoneNumber("01011112223").orElseThrow();
        Member member2 = memberRepository.findByPhoneNumber("01011112224").orElseThrow();
        Member member3 = memberRepository.findByPhoneNumber("01011112225").orElseThrow();
        Team aTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();
        ShiftType Off = shiftRepository.findByWardIdAndName(ward.getId(), ShiftType.OFF).orElseThrow();
        requestRepository.save(new Request(member1, Off, LocalDate.of(2025, 9, 15), LocalDate.of(2025, 9, 16)));
        requestRepository.save(new Request(member2, Off, LocalDate.of(2025, 9, 15), LocalDate.of(2025, 9, 15)));

        Map<Long, Map<Integer, DraftCell>> board = new HashMap<>();
        Map<Integer, DraftCell> days = new HashMap<>();

        for (int d = 1; d <= 30; d++) {
            days.put(d, new DraftCell(member3.getId(), member3.getName(), aTeam.getId(), aTeam.getName(), d, null, null, true));
        }
        board.put(member3.getId(), days);

        DraftPayload payload = new DraftPayload(board);
        Schedule schedule = scheduleRepository.save(new Schedule(ward, "2025-09"));
        Draft draft = draftRepository.save(new Draft(schedule));
        draft.updatePayload(payload);
        Assertions.assertDoesNotThrow(() -> preCheck.preCheckWard(ward.getId(), schedule.getId()));
    }

    @Test
    @DisplayName("주말: 전체 팀 인원 6명, 주말 기준 하루에 3명이 필요, 필요량 보다 휴가 더 많이 신청 시 예외")
    void 주말_기준_휴가_신청_통과() {
        Ward ward = getWard();
        init(ward);

        Member member1 = memberRepository.findByPhoneNumber("01011112223").orElseThrow();
        Member member2 = memberRepository.findByPhoneNumber("01011112224").orElseThrow();
        Member member3 = memberRepository.findByPhoneNumber("01011112225").orElseThrow();
        Team aTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();
        ShiftType Off = shiftRepository.findByWardIdAndName(ward.getId(), ShiftType.OFF).orElseThrow();
        requestRepository.save(new Request(member1, Off, LocalDate.of(2025, 9, 19), LocalDate.of(2025, 9, 20)));
        requestRepository.save(new Request(member2, Off, LocalDate.of(2025, 9, 20), LocalDate.of(2025, 9, 21)));
        requestRepository.save(new Request(member3, Off, LocalDate.of(2025, 9, 20), LocalDate.of(2025, 9, 20)));

        Map<Long, Map<Integer, DraftCell>> board = new HashMap<>();
        Map<Integer, DraftCell> days = new HashMap<>();
        for (int d = 1; d <= 30; d++) {
            days.put(d, new DraftCell(member1.getId(), member1.getName(), aTeam.getId(), aTeam.getName(), d, null, null, true));
        }
        board.put(member1.getId(), days);
        Schedule schedule = scheduleRepository.save(new Schedule(ward, "2025-09"));
        Draft draft = draftRepository.save(new Draft(schedule));
        DraftPayload payload = new DraftPayload(board);
        draft.updatePayload(payload);
        Assertions.assertDoesNotThrow(() -> preCheck.preCheckWard(ward.getId(), schedule.getId()));
    }


    /// ///예외 테스트
    @Test
    @DisplayName("전체 팀 인원 6명, 평일 기준 하루에 4명 필요, 3명 이상 같은 날 휴가 신청 시 예외 발생")
    void 휴가를_기준_보다_너무_많이_신청_하면_예외_평일() {
        Ward ward = getWard();
        init(ward);
        Member member1 = memberRepository.findByPhoneNumber("01011112223").orElseThrow();
        Member member2 = memberRepository.findByPhoneNumber("01011112224").orElseThrow();
        Member member3 = memberRepository.findByPhoneNumber("01011112225").orElseThrow();
        Team aTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();
        ShiftType Off = shiftRepository.findByWardIdAndName(ward.getId(), ShiftType.OFF).orElseThrow();

        Map<Long, Map<Integer, DraftCell>> board = new HashMap<>();
        Map<Integer, DraftCell> member3Days = new HashMap<>();
        Map<Integer, DraftCell> member2Days = new HashMap<>();
        Map<Integer, DraftCell> member1Days = new HashMap<>();
        for (int d = 1; d <= 30; d++) {
            if (d == 15) {
                member1Days.put(d, new DraftCell(member1.getId(), member1.getName(), aTeam.getId(), aTeam.getName(), d, Off.getId(), ShiftType.OFF, true));
                member2Days.put(d, new DraftCell(member2.getId(), member2.getName(), aTeam.getId(), aTeam.getName(), d, Off.getId(), ShiftType.OFF, true));
                member3Days.put(d, new DraftCell(member3.getId(), member3.getName(), aTeam.getId(), aTeam.getName(), d, Off.getId(), ShiftType.OFF, true));
            } else {
                member1Days.put(d, new DraftCell(member1.getId(), member1.getName(), aTeam.getId(), aTeam.getName(), d, null, null, true));
                member2Days.put(d, new DraftCell(member2.getId(), member2.getName(), aTeam.getId(), aTeam.getName(), d, null, null, true));
                member3Days.put(d, new DraftCell(member3.getId(), member3.getName(), aTeam.getId(), aTeam.getName(), d, null, null, true));
            }
        }
        board.put(member3.getId(), member3Days);
        board.put(member1.getId(), member1Days);
        board.put(member2.getId(), member2Days);

        DraftPayload payload = new DraftPayload(board);
        Schedule schedule = scheduleRepository.save(new Schedule(ward, "2025-09"));
        Draft saved = draftRepository.save(new Draft(schedule));
        saved.updatePayload(payload);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            preCheck.preCheckWard(ward.getId(), schedule.getId());
        });
    }

    @Test
    @DisplayName("전체 팀 인원 6명, 평일 기준 하루에 3명 필요, 4명 이상 같은 날 휴가 신청 시 예외 발생")
    void 휴가를_기준_보다_너무_많이_신청_하면_예외_주말() {
        Ward ward = getWard();
        Team team = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();
        init(ward);

        Member member1 = memberRepository.findByPhoneNumber("01011112223").orElseThrow();
        Map<Integer, DraftCell> m1Cell = new HashMap<>();

        Member member2 = memberRepository.findByPhoneNumber("01011112224").orElseThrow();
        Map<Integer, DraftCell> m2Cell = new HashMap<>();

        Member member3 = memberRepository.findByPhoneNumber("01011112225").orElseThrow();
        Map<Integer, DraftCell> m3Cell = new HashMap<>();

        Member member4 = memberRepository.findByPhoneNumber("01011112226").orElseThrow();
        Map<Integer, DraftCell> m4Cell = new HashMap<>();

        ShiftType Off = shiftRepository.findByWardIdAndName(ward.getId(), ShiftType.OFF).orElseThrow();

        Map<Long, Map<Integer, DraftCell>> board = new HashMap<>();

        for (int d = 1; d <= 30; d++) {
            if (d == 20) {
                m1Cell.put(d, new DraftCell(member1.getId(), member1.getName(), team.getId(), team.getName(), d, Off.getId(), ShiftType.OFF, true));
                m2Cell.put(d, new DraftCell(member2.getId(), member2.getName(), team.getId(), team.getName(), d, Off.getId(), ShiftType.OFF, true));
                m3Cell.put(d, new DraftCell(member3.getId(), member3.getName(), team.getId(), team.getName(), d, Off.getId(), ShiftType.OFF, true));
                m4Cell.put(d, new DraftCell(member4.getId(), member4.getName(), team.getId(), team.getName(), d, Off.getId(), ShiftType.OFF, true));
            } else {
                m1Cell.put(d, new DraftCell(member1.getId(), member1.getName(), team.getId(), team.getName(), d, null, null, true));
                m2Cell.put(d, new DraftCell(member2.getId(), member2.getName(), team.getId(), team.getName(), d, null, null, true));
                m3Cell.put(d, new DraftCell(member3.getId(), member3.getName(), team.getId(), team.getName(), d, null, null, true));
                m4Cell.put(d, new DraftCell(member4.getId(), member4.getName(), team.getId(), team.getName(), d, null, null, true));
            }
        }
        board.put(member1.getId(), m1Cell);
        board.put(member2.getId(), m2Cell);
        board.put(member3.getId(), m3Cell);
        board.put(member4.getId(), m4Cell);

        Schedule schedule = scheduleRepository.save(new Schedule(ward, "2025-09"));
        Draft draft = draftRepository.save(new Draft(schedule));
        DraftPayload payload = new DraftPayload(board);
        draft.updatePayload(payload);

        Assertions.assertThrows(IllegalArgumentException.class, () -> preCheck.preCheckWard(ward.getId(), schedule.getId()));
    }

}
