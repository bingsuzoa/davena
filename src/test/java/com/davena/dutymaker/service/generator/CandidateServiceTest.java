package com.davena.dutymaker.service.generator;

import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftCell;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.domain.Request;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.member.MemberAllowedShift;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.*;
import com.davena.dutymaker.service.BackfillService;
import com.davena.dutymaker.service.DraftService;
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
        GeneratorService.class,
        MemberStateService.class,
        TeamStateService.class,
        DraftService.class,
        CandidateService.class,
        HardPolicyFilter.class,
        BackfillService.class
})
@ActiveProfiles("test")
public class CandidateServiceTest {


    @Autowired
    private RequirementRuleRepository ruleRepository;
    @Autowired
    private DraftRepository draftRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private MemberStateService memberStateService;
    @Autowired
    private TeamStateService teamStateService;
    @Autowired
    private HardPolicyFilter hardPolicyFilter;
    @Autowired
    private CandidateAssignmentRepository candidateAssignmentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private SkillGradeRepository gradeRepository;
    @Autowired
    private ShiftTypeRepository shiftRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private DraftService draftService;
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private BackfillService backfillService;
    @Autowired
    private MemberAllowedShiftRepository allowedShiftRepository;
    @Autowired
    private GeneratorService generatorService;

    Ward getSettedWard() {
        Member supervisor = memberRepository.save(new Member("이진이", "이진이", "01011112221", "1234"));
        Hospital hospital = hospitalRepository.save(new Hospital());
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));

        Team aTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();
        Team bTeam = teamRepository.save(new Team(ward, "bTeam"));

        SkillGrade grade1 = gradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();
        SkillGrade grade2 = gradeRepository.save(new SkillGrade(ward, "grade2"));
        SkillGrade grade3 = gradeRepository.save(new SkillGrade(ward, "grade3"));
        SkillGrade grade4 = gradeRepository.save(new SkillGrade(ward, "grade4"));

        ShiftType off = shiftRepository.save(new ShiftType(ward, "off", LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, false));
        ShiftType D = shiftRepository.save(new ShiftType(ward, "day", LocalTime.of(7, 30), LocalTime.of(16, 30), true));
        ShiftType E = shiftRepository.save(new ShiftType(ward, "eve", LocalTime.of(16, 0), LocalTime.of(0, 0), true));
        ShiftType N = shiftRepository.save(new ShiftType(ward, "nig", LocalTime.of(0, 0), LocalTime.of(8, 0), true));

        Schedule schedule = scheduleRepository.save(new Schedule(ward, "2025-08"));
        Map<Long, Map<Integer, DraftCell>> cells = new HashMap<>();

        Member m1 = memberRepository.save(new Member("여나림", "여나림", "01011112222", "1234"));
        m1.isCharge(true, 1);
        m1.joinWard(ward, aTeam, grade1);
        Map<Integer, DraftCell> m1cell = new HashMap<>();
        m1cell.put(25, new DraftCell(m1.getId(), m1.getName(), aTeam.getId(), aTeam.getName(), 25, off.getId(), off.getName(), true));
        m1cell.put(26, new DraftCell(m1.getId(), m1.getName(), aTeam.getId(), aTeam.getName(), 26, D.getId(), D.getName(), true));
        m1cell.put(27, new DraftCell(m1.getId(), m1.getName(), aTeam.getId(), aTeam.getName(), 27, D.getId(), D.getName(), true));
        m1cell.put(28, new DraftCell(m1.getId(), m1.getName(), aTeam.getId(), aTeam.getName(), 28, D.getId(), D.getName(), true));
        m1cell.put(29, new DraftCell(m1.getId(), m1.getName(), aTeam.getId(), aTeam.getName(), 29, D.getId(), D.getName(), true));
        m1cell.put(30, new DraftCell(m1.getId(), m1.getName(), aTeam.getId(), aTeam.getName(), 30, off.getId(), off.getName(), true));
        m1cell.put(31, new DraftCell(m1.getId(), m1.getName(), aTeam.getId(), aTeam.getName(), 31, off.getId(), off.getName(), true));
        cells.put(m1.getId(), m1cell);
        requestRepository.save(new Request(m1, off, LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 10)));
        requestRepository.save(new Request(m1, off, LocalDate.of(2025, 9, 6), LocalDate.of(2025, 9, 7)));
        requestRepository.save(new Request(m1, off, LocalDate.of(2025, 9, 12), LocalDate.of(2025, 9, 12)));
        allowedShiftRepository.save(new MemberAllowedShift(m1, D));
        allowedShiftRepository.save(new MemberAllowedShift(m1, off));

        Member m2 = memberRepository.save(new Member("김혜준", "김혜준", "01011112223", "1234"));
        m2.isCharge(true, 2);
        m2.joinWard(ward, aTeam, grade1);
        Map<Integer, DraftCell> m2cell = new HashMap<>();
        m2cell.put(25, new DraftCell(m2.getId(), m2.getName(), aTeam.getId(), aTeam.getName(), 25, N.getId(), N.getName(), true));
        m2cell.put(26, new DraftCell(m2.getId(), m2.getName(), aTeam.getId(), aTeam.getName(), 26, off.getId(), off.getName(), true));
        m2cell.put(27, new DraftCell(m2.getId(), m2.getName(), aTeam.getId(), aTeam.getName(), 27, off.getId(), off.getName(), true));
        m2cell.put(28, new DraftCell(m2.getId(), m2.getName(), aTeam.getId(), aTeam.getName(), 28, D.getId(), D.getName(), true));
        m2cell.put(29, new DraftCell(m2.getId(), m2.getName(), aTeam.getId(), aTeam.getName(), 29, D.getId(), D.getName(), true));
        m2cell.put(30, new DraftCell(m2.getId(), m2.getName(), aTeam.getId(), aTeam.getName(), 30, D.getId(), D.getName(), true));
        m2cell.put(31, new DraftCell(m2.getId(), m2.getName(), aTeam.getId(), aTeam.getName(), 31, off.getId(), off.getName(), true));
        cells.put(m2.getId(), m2cell);
        requestRepository.save(new Request(m2, off, LocalDate.of(2025, 9, 6), LocalDate.of(2025, 9, 7)));
        requestRepository.save(new Request(m2, off, LocalDate.of(2025, 9, 8), LocalDate.of(2025, 9, 10)));
        requestRepository.save(new Request(m2, off, LocalDate.of(2025, 9, 11), LocalDate.of(2025, 9, 11)));
        allowedShiftRepository.save(new MemberAllowedShift(m2, D));
        allowedShiftRepository.save(new MemberAllowedShift(m2, E));
        allowedShiftRepository.save(new MemberAllowedShift(m2, N));
        allowedShiftRepository.save(new MemberAllowedShift(m2, off));

        Member m3 = memberRepository.save(new Member("최지민", "최지민", "01011112224", "1234"));
        m3.isCharge(true, 2);
        m3.joinWard(ward, aTeam, grade1);
        Map<Integer, DraftCell> m3cell = new HashMap<>();
        m3cell.put(25, new DraftCell(m3.getId(), m3.getName(), aTeam.getId(), aTeam.getName(), 25, off.getId(), off.getName(), true));
        m3cell.put(26, new DraftCell(m3.getId(), m3.getName(), aTeam.getId(), aTeam.getName(), 26, off.getId(), off.getName(), true));
        m3cell.put(27, new DraftCell(m3.getId(), m3.getName(), aTeam.getId(), aTeam.getName(), 27, off.getId(), off.getName(), true));
        m3cell.put(28, new DraftCell(m3.getId(), m3.getName(), aTeam.getId(), aTeam.getName(), 28, off.getId(), off.getName(), true));
        m3cell.put(29, new DraftCell(m3.getId(), m3.getName(), aTeam.getId(), aTeam.getName(), 29, off.getId(), off.getName(), true));
        m3cell.put(30, new DraftCell(m3.getId(), m3.getName(), aTeam.getId(), aTeam.getName(), 30, off.getId(), off.getName(), true));
        m3cell.put(31, new DraftCell(m3.getId(), m3.getName(), aTeam.getId(), aTeam.getName(), 31, off.getId(), off.getName(), true));
        cells.put(m3.getId(), m3cell);
        allowedShiftRepository.save(new MemberAllowedShift(m3, D));
        allowedShiftRepository.save(new MemberAllowedShift(m3, off));


        Member m4 = memberRepository.save(new Member("최수아", "최수아", "01011112225", "1234"));
        m4.joinWard(ward, aTeam, grade2);
        Map<Integer, DraftCell> m4cell = new HashMap<>();
        m4cell.put(25, new DraftCell(m4.getId(), m4.getName(), aTeam.getId(), aTeam.getName(), 25, E.getId(), E.getName(), false));
        m4cell.put(26, new DraftCell(m4.getId(), m4.getName(), aTeam.getId(), aTeam.getName(), 26, N.getId(), N.getName(), false));
        m4cell.put(27, new DraftCell(m4.getId(), m4.getName(), aTeam.getId(), aTeam.getName(), 27, N.getId(), N.getName(), false));
        m4cell.put(28, new DraftCell(m4.getId(), m4.getName(), aTeam.getId(), aTeam.getName(), 28, N.getId(), N.getName(), false));
        m4cell.put(29, new DraftCell(m4.getId(), m4.getName(), aTeam.getId(), aTeam.getName(), 29, off.getId(), off.getName(), false));
        m4cell.put(30, new DraftCell(m4.getId(), m4.getName(), aTeam.getId(), aTeam.getName(), 30, off.getId(), off.getName(), false));
        m4cell.put(31, new DraftCell(m4.getId(), m4.getName(), aTeam.getId(), aTeam.getName(), 31, D.getId(), D.getName(), false));
        cells.put(m4.getId(), m4cell);
        requestRepository.save(new Request(m4, off, LocalDate.of(2025, 9, 6), LocalDate.of(2025, 9, 8)));
        allowedShiftRepository.save(new MemberAllowedShift(m4, D));
        allowedShiftRepository.save(new MemberAllowedShift(m4, E));
        allowedShiftRepository.save(new MemberAllowedShift(m4, N));
        allowedShiftRepository.save(new MemberAllowedShift(m4, off));


        Member m5 = memberRepository.save(new Member("오세희", "오세희", "01011112226", "1234"));
        m5.joinWard(ward, aTeam, grade3);
        Map<Integer, DraftCell> m5cell = new HashMap<>();
        m5cell.put(25, new DraftCell(m5.getId(), m5.getName(), aTeam.getId(), aTeam.getName(), 25, N.getId(), N.getName(), false));
        m5cell.put(26, new DraftCell(m5.getId(), m5.getName(), aTeam.getId(), aTeam.getName(), 26, off.getId(), off.getName(), false));
        m5cell.put(27, new DraftCell(m5.getId(), m5.getName(), aTeam.getId(), aTeam.getName(), 27, off.getId(), off.getName(), false));
        m5cell.put(28, new DraftCell(m5.getId(), m5.getName(), aTeam.getId(), aTeam.getName(), 28, off.getId(), off.getName(), false));
        m5cell.put(29, new DraftCell(m5.getId(), m5.getName(), aTeam.getId(), aTeam.getName(), 29, D.getId(), D.getName(), false));
        m5cell.put(30, new DraftCell(m5.getId(), m5.getName(), aTeam.getId(), aTeam.getName(), 30, D.getId(), D.getName(), false));
        m5cell.put(31, new DraftCell(m5.getId(), m5.getName(), aTeam.getId(), aTeam.getName(), 31, E.getId(), E.getName(), false));
        cells.put(m5.getId(), m5cell);
        requestRepository.save(new Request(m5, off, LocalDate.of(2025, 9, 5), LocalDate.of(2025, 9, 5)));
        requestRepository.save(new Request(m5, off, LocalDate.of(2025, 9, 12), LocalDate.of(2025, 9, 12)));
        allowedShiftRepository.save(new MemberAllowedShift(m5, D));
        allowedShiftRepository.save(new MemberAllowedShift(m5, E));
        allowedShiftRepository.save(new MemberAllowedShift(m5, N));
        allowedShiftRepository.save(new MemberAllowedShift(m5, off));


        Member m6 = memberRepository.save(new Member("김상민", "김상민", "01011112227", "1234"));
        m6.joinWard(ward, aTeam, grade3);
        Map<Integer, DraftCell> m6cell = new HashMap<>();
        m6cell.put(25, new DraftCell(m6.getId(), m6.getName(), aTeam.getId(), aTeam.getName(), 25, off.getId(), off.getName(), m6.isCharge()));
        m6cell.put(26, new DraftCell(m6.getId(), m6.getName(), aTeam.getId(), aTeam.getName(), 26, off.getId(), off.getName(), m6.isCharge()));
        m6cell.put(27, new DraftCell(m6.getId(), m6.getName(), aTeam.getId(), aTeam.getName(), 27, off.getId(), off.getName(), m6.isCharge()));
        m6cell.put(28, new DraftCell(m6.getId(), m6.getName(), aTeam.getId(), aTeam.getName(), 28, E.getId(), E.getName(), m6.isCharge()));
        m6cell.put(29, new DraftCell(m6.getId(), m6.getName(), aTeam.getId(), aTeam.getName(), 29, N.getId(), N.getName(), m6.isCharge()));
        m6cell.put(30, new DraftCell(m6.getId(), m6.getName(), aTeam.getId(), aTeam.getName(), 30, N.getId(), N.getName(), m6.isCharge()));
        m6cell.put(31, new DraftCell(m6.getId(), m6.getName(), aTeam.getId(), aTeam.getName(), 31, N.getId(), N.getName(), m6.isCharge()));
        cells.put(m6.getId(), m6cell);
        requestRepository.save(new Request(m6, off, LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 12)));
        allowedShiftRepository.save(new MemberAllowedShift(m6, D));
        allowedShiftRepository.save(new MemberAllowedShift(m6, E));
        allowedShiftRepository.save(new MemberAllowedShift(m6, N));
        allowedShiftRepository.save(new MemberAllowedShift(m6, off));


        Member m7 = memberRepository.save(new Member("서주연", "서주연", "01011112228", "1234"));
        Map<Integer, DraftCell> m7cell = new HashMap<>();
        m7cell.put(25, new DraftCell(m7.getId(), m7.getName(), aTeam.getId(), aTeam.getName(), 25, D.getId(), D.getName(), m7.isCharge()));
        m7cell.put(26, new DraftCell(m7.getId(), m7.getName(), aTeam.getId(), aTeam.getName(), 26, D.getId(), D.getName(), m7.isCharge()));
        m7cell.put(27, new DraftCell(m7.getId(), m7.getName(), aTeam.getId(), aTeam.getName(), 27, E.getId(), E.getName(), m7.isCharge()));
        m7cell.put(28, new DraftCell(m7.getId(), m7.getName(), aTeam.getId(), aTeam.getName(), 28, off.getId(), off.getName(), m7.isCharge()));
        m7cell.put(29, new DraftCell(m7.getId(), m7.getName(), aTeam.getId(), aTeam.getName(), 29, N.getId(), N.getName(), m7.isCharge()));
        m7cell.put(30, new DraftCell(m7.getId(), m7.getName(), aTeam.getId(), aTeam.getName(), 30, N.getId(), N.getName(), m7.isCharge()));
        m7cell.put(31, new DraftCell(m7.getId(), m7.getName(), aTeam.getId(), aTeam.getName(), 31, N.getId(), N.getName(), m7.isCharge()));
        cells.put(m7.getId(), m7cell);
        m7.joinWard(ward, aTeam, grade3);
        allowedShiftRepository.save(new MemberAllowedShift(m7, D));
        allowedShiftRepository.save(new MemberAllowedShift(m7, E));
        allowedShiftRepository.save(new MemberAllowedShift(m7, N));
        allowedShiftRepository.save(new MemberAllowedShift(m7, off));



        Member m8 = memberRepository.save(new Member("김소윤", "김소윤", "01011112229", "1234"));
        m8.joinWard(ward, aTeam, grade2);
        Map<Integer, DraftCell> m8cell = new HashMap<>();
        m8cell.put(25, new DraftCell(m8.getId(), m8.getName(), aTeam.getId(), aTeam.getName(), 25, D.getId(), D.getName(), m8.isCharge()));
        m8cell.put(26, new DraftCell(m8.getId(), m8.getName(), aTeam.getId(), aTeam.getName(), 26, D.getId(), D.getName(), m8.isCharge()));
        m8cell.put(27, new DraftCell(m8.getId(), m8.getName(), aTeam.getId(), aTeam.getName(), 27, D.getId(), D.getName(), m8.isCharge()));
        m8cell.put(28, new DraftCell(m8.getId(), m8.getName(), aTeam.getId(), aTeam.getName(), 28, off.getId(), off.getName(), m8.isCharge()));
        m8cell.put(29, new DraftCell(m8.getId(), m8.getName(), aTeam.getId(), aTeam.getName(), 29, off.getId(), off.getName(), m8.isCharge()));
        m8cell.put(30, new DraftCell(m8.getId(), m8.getName(), aTeam.getId(), aTeam.getName(), 30, off.getId(), off.getName(), m8.isCharge()));
        m8cell.put(31, new DraftCell(m8.getId(), m8.getName(), aTeam.getId(), aTeam.getName(), 31, off.getId(), off.getName(), m8.isCharge()));
        cells.put(m8.getId(), m8cell);
        allowedShiftRepository.save(new MemberAllowedShift(m8, D));
        allowedShiftRepository.save(new MemberAllowedShift(m8, E));
        allowedShiftRepository.save(new MemberAllowedShift(m8, N));
        allowedShiftRepository.save(new MemberAllowedShift(m8, off));


        Member m9 = memberRepository.save(new Member("조세음", "조세음", "01011112210", "1234"));
        m9.joinWard(ward, aTeam, grade4);
        Map<Integer, DraftCell> m9cell = new HashMap<>();
        m9cell.put(25, new DraftCell(m9.getId(), m9.getName(), aTeam.getId(), aTeam.getName(), 25, E.getId(), E.getName(), m9.isCharge()));
        m9cell.put(26, new DraftCell(m9.getId(), m9.getName(), aTeam.getId(), aTeam.getName(), 26, N.getId(), N.getName(), m9.isCharge()));
        m9cell.put(27, new DraftCell(m9.getId(), m9.getName(), aTeam.getId(), aTeam.getName(), 27, N.getId(), N.getName(), m9.isCharge()));
        m9cell.put(28, new DraftCell(m9.getId(), m9.getName(), aTeam.getId(), aTeam.getName(), 28, N.getId(), N.getName(), m9.isCharge()));
        m9cell.put(29, new DraftCell(m9.getId(), m9.getName(), aTeam.getId(), aTeam.getName(), 29, off.getId(), off.getName(), m9.isCharge()));
        m9cell.put(30, new DraftCell(m9.getId(), m9.getName(), aTeam.getId(), aTeam.getName(), 30, off.getId(), off.getName(), m9.isCharge()));
        m9cell.put(31, new DraftCell(m9.getId(), m9.getName(), aTeam.getId(), aTeam.getName(), 31, D.getId(), D.getName(), m9.isCharge()));
        cells.put(m9.getId(), m9cell);
        allowedShiftRepository.save(new MemberAllowedShift(m9, D));
        allowedShiftRepository.save(new MemberAllowedShift(m9, E));
        allowedShiftRepository.save(new MemberAllowedShift(m9, N));
        allowedShiftRepository.save(new MemberAllowedShift(m9, off));
        requestRepository.save(new Request(m9, off, LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 11)));

        Member m10 = memberRepository.save(new Member("이정화", "이정화", "01011112211", "1234"));
        m10.isCharge(true, 1);
        m10.joinWard(ward, bTeam, grade1);
        Map<Integer, DraftCell> m10cell = new HashMap<>();
        m10cell.put(25, new DraftCell(m10.getId(), m10.getName(), bTeam.getId(), bTeam.getName(), 25, D.getId(), D.getName(), m10.isCharge()));
        m10cell.put(26, new DraftCell(m10.getId(), m10.getName(), bTeam.getId(), bTeam.getName(), 26, D.getId(), D.getName(), m10.isCharge()));
        m10cell.put(27, new DraftCell(m10.getId(), m10.getName(), bTeam.getId(), bTeam.getName(), 27, D.getId(), D.getName(), m10.isCharge()));
        m10cell.put(28, new DraftCell(m10.getId(), m10.getName(), bTeam.getId(), bTeam.getName(), 28, off.getId(), off.getName(), m10.isCharge()));
        m10cell.put(29, new DraftCell(m10.getId(), m10.getName(), bTeam.getId(), bTeam.getName(), 29, off.getId(), off.getName(), m10.isCharge()));
        m10cell.put(30, new DraftCell(m10.getId(), m10.getName(), bTeam.getId(), bTeam.getName(), 30, off.getId(), off.getName(), m10.isCharge()));
        m10cell.put(31, new DraftCell(m10.getId(), m10.getName(), bTeam.getId(), bTeam.getName(), 31, off.getId(), off.getName(), m10.isCharge()));
        cells.put(m10.getId(), m10cell);
        allowedShiftRepository.save(new MemberAllowedShift(m10, D));
        allowedShiftRepository.save(new MemberAllowedShift(m10, off));
        requestRepository.save(new Request(m10, off, LocalDate.of(2025, 9, 13), LocalDate.of(2025, 9, 15)));
        requestRepository.save(new Request(m10, off, LocalDate.of(2025, 9, 19), LocalDate.of(2025, 9, 20)));

        Member m11 = memberRepository.save(new Member("임선우", "임선우", "01011112212", "1234"));
        m11.isCharge(true, 2);
        m11.joinWard(ward, bTeam, grade1);
        Map<Integer, DraftCell> m11cell = new HashMap<>();
        m11cell.put(25, new DraftCell(m11.getId(), m11.getName(), bTeam.getId(), bTeam.getName(), 25, off.getId(), off.getName(), m11.isCharge()));
        m11cell.put(26, new DraftCell(m11.getId(), m11.getName(), bTeam.getId(), bTeam.getName(), 26, E.getId(), E.getName(), m11.isCharge()));
        m11cell.put(27, new DraftCell(m11.getId(), m11.getName(), bTeam.getId(), bTeam.getName(), 27, E.getId(), E.getName(), m11.isCharge()));
        m11cell.put(28, new DraftCell(m11.getId(), m11.getName(), bTeam.getId(), bTeam.getName(), 28, E.getId(), E.getName(), m11.isCharge()));
        m11cell.put(29, new DraftCell(m11.getId(), m11.getName(), bTeam.getId(), bTeam.getName(), 29, E.getId(), E.getName(), m11.isCharge()));
        m11cell.put(30, new DraftCell(m11.getId(), m11.getName(), bTeam.getId(), bTeam.getName(), 30, N.getId(), N.getName(), m11.isCharge()));
        m11cell.put(31, new DraftCell(m11.getId(), m11.getName(), bTeam.getId(), bTeam.getName(), 31, N.getId(), N.getName(), m11.isCharge()));
        cells.put(m11.getId(), m11cell);
        requestRepository.save(new Request(m11, off, LocalDate.of(2025, 9, 19), LocalDate.of(2025, 9, 22)));
        allowedShiftRepository.save(new MemberAllowedShift(m11, D));
        allowedShiftRepository.save(new MemberAllowedShift(m11, E));
        allowedShiftRepository.save(new MemberAllowedShift(m11, N));
        allowedShiftRepository.save(new MemberAllowedShift(m11, off));



        Member m12 = memberRepository.save(new Member("이규복", "이규복", "01011112213", "1234"));
        m12.isCharge(true, 2);
        m12.joinWard(ward, bTeam, grade1);
        Map<Integer, DraftCell> m12cell = new HashMap<>();
        m12cell.put(25, new DraftCell(m12.getId(), m12.getName(), bTeam.getId(), bTeam.getName(), 25, D.getId(), D.getName(), m12.isCharge()));
        m12cell.put(26, new DraftCell(m12.getId(), m12.getName(), bTeam.getId(), bTeam.getName(), 26, D.getId(), D.getName(), m12.isCharge()));
        m12cell.put(27, new DraftCell(m12.getId(), m12.getName(), bTeam.getId(), bTeam.getName(), 27, D.getId(), D.getName(), m12.isCharge()));
        m12cell.put(28, new DraftCell(m12.getId(), m12.getName(), bTeam.getId(), bTeam.getName(), 28, D.getId(), D.getName(), m12.isCharge()));
        m12cell.put(29, new DraftCell(m12.getId(), m12.getName(), bTeam.getId(), bTeam.getName(), 29, D.getId(), D.getName(), m12.isCharge()));
        m12cell.put(30, new DraftCell(m12.getId(), m12.getName(), bTeam.getId(), bTeam.getName(), 30, off.getId(), off.getName(), m12.isCharge()));
        m12cell.put(31, new DraftCell(m12.getId(), m12.getName(), bTeam.getId(), bTeam.getName(), 31, D.getId(), D.getName(), m12.isCharge()));
        cells.put(m12.getId(), m12cell);
        m12.joinWard(ward, bTeam, grade1);
        allowedShiftRepository.save(new MemberAllowedShift(m12, D));
        allowedShiftRepository.save(new MemberAllowedShift(m12, E));
        allowedShiftRepository.save(new MemberAllowedShift(m12, N));
        allowedShiftRepository.save(new MemberAllowedShift(m12, off));

        Member m13 = memberRepository.save(new Member("이준범", "이준범", "01011112214", "1234"));
        m13.isCharge(true, 2);
        m13.joinWard(ward, bTeam, grade1);
        Map<Integer, DraftCell> m13cell = new HashMap<>();
        m13cell.put(25, new DraftCell(m13.getId(), m13.getName(), bTeam.getId(), bTeam.getName(), 25, off.getId(), off.getName(), m13.isCharge()));
        m13cell.put(26, new DraftCell(m13.getId(), m13.getName(), bTeam.getId(), bTeam.getName(), 26, off.getId(), off.getName(), m13.isCharge()));
        m13cell.put(27, new DraftCell(m13.getId(), m13.getName(), bTeam.getId(), bTeam.getName(), 27, D.getId(), D.getName(), m13.isCharge()));
        m13cell.put(28, new DraftCell(m13.getId(), m13.getName(), bTeam.getId(), bTeam.getName(), 28, D.getId(), D.getName(), m13.isCharge()));
        m13cell.put(29, new DraftCell(m13.getId(), m13.getName(), bTeam.getId(), bTeam.getName(), 29, D.getId(), D.getName(), m13.isCharge()));
        m13cell.put(30, new DraftCell(m13.getId(), m13.getName(), bTeam.getId(), bTeam.getName(), 30, off.getId(), off.getName(), m13.isCharge()));
        m13cell.put(31, new DraftCell(m13.getId(), m13.getName(), bTeam.getId(), bTeam.getName(), 31, off.getId(), off.getName(), m13.isCharge()));
        cells.put(m13.getId(), m13cell);
        requestRepository.save(new Request(m13, off, LocalDate.of(2025, 9, 13), LocalDate.of(2025, 9, 13)));
        allowedShiftRepository.save(new MemberAllowedShift(m13, D));
        allowedShiftRepository.save(new MemberAllowedShift(m13, E));
        allowedShiftRepository.save(new MemberAllowedShift(m13, N));
        allowedShiftRepository.save(new MemberAllowedShift(m13, off));



        Member m14 = memberRepository.save(new Member("조아영", "조아영", "01011112215", "1234"));
        m14.joinWard(ward, bTeam, grade3);
        Map<Integer, DraftCell> m14cell = new HashMap<>();
        m14cell.put(25, new DraftCell(m14.getId(), m14.getName(), bTeam.getId(), bTeam.getName(), 25, N.getId(), N.getName(), m14.isCharge()));
        m14cell.put(26, new DraftCell(m14.getId(), m14.getName(), bTeam.getId(), bTeam.getName(), 26, N.getId(), N.getName(), m14.isCharge()));
        m14cell.put(27, new DraftCell(m14.getId(), m14.getName(), bTeam.getId(), bTeam.getName(), 27, off.getId(), off.getName(), m14.isCharge()));
        m14cell.put(28, new DraftCell(m14.getId(), m14.getName(), bTeam.getId(), bTeam.getName(), 28, off.getId(), off.getName(), m14.isCharge()));
        m14cell.put(29, new DraftCell(m14.getId(), m14.getName(), bTeam.getId(), bTeam.getName(), 29, off.getId(), off.getName(), m14.isCharge()));
        m14cell.put(30, new DraftCell(m14.getId(), m14.getName(), bTeam.getId(), bTeam.getName(), 30, D.getId(), D.getName(), m14.isCharge()));
        m14cell.put(31, new DraftCell(m14.getId(), m14.getName(), bTeam.getId(), bTeam.getName(), 31, off.getId(), off.getName(), m14.isCharge()));
        cells.put(m14.getId(), m14cell);
        allowedShiftRepository.save(new MemberAllowedShift(m14, D));
        allowedShiftRepository.save(new MemberAllowedShift(m14, E));
        allowedShiftRepository.save(new MemberAllowedShift(m14, N));
        allowedShiftRepository.save(new MemberAllowedShift(m14, off));


        Member m15 = memberRepository.save(new Member("이경민", "이경민", "01011112216", "1234"));
        m15.joinWard(ward, bTeam, grade2);
        Map<Integer, DraftCell> m15cell = new HashMap<>();
        m15cell.put(25, new DraftCell(m15.getId(), m15.getName(), bTeam.getId(), bTeam.getName(), 25, off.getId(), off.getName(), m15.isCharge()));
        m15cell.put(26, new DraftCell(m15.getId(), m15.getName(), bTeam.getId(), bTeam.getName(), 26, off.getId(), off.getName(), m15.isCharge()));
        m15cell.put(27, new DraftCell(m15.getId(), m15.getName(), bTeam.getId(), bTeam.getName(), 27, off.getId(), off.getName(), m15.isCharge()));
        m15cell.put(28, new DraftCell(m15.getId(), m15.getName(), bTeam.getId(), bTeam.getName(), 28, D.getId(), D.getName(), m15.isCharge()));
        m15cell.put(29, new DraftCell(m15.getId(), m15.getName(), bTeam.getId(), bTeam.getName(), 29, off.getId(), off.getName(), m15.isCharge()));
        m15cell.put(30, new DraftCell(m15.getId(), m15.getName(), bTeam.getId(), bTeam.getName(), 30, off.getId(), off.getName(), m15.isCharge()));
        m15cell.put(31, new DraftCell(m15.getId(), m15.getName(), bTeam.getId(), bTeam.getName(), 31, E.getId(), E.getName(), m15.isCharge()));
        cells.put(m15.getId(), m15cell);
        allowedShiftRepository.save(new MemberAllowedShift(m15, D));
        allowedShiftRepository.save(new MemberAllowedShift(m15, E));
        allowedShiftRepository.save(new MemberAllowedShift(m15, N));
        allowedShiftRepository.save(new MemberAllowedShift(m15, off));


        Member m16 = memberRepository.save(new Member("한석희", "한석희", "01011112217", "1234"));
        m16.joinWard(ward, bTeam, grade2);
        Map<Integer, DraftCell> m16cell = new HashMap<>();
        m16cell.put(25, new DraftCell(m16.getId(), m16.getName(), bTeam.getId(), bTeam.getName(), 25, E.getId(), E.getName(), m16.isCharge()));
        m16cell.put(26, new DraftCell(m16.getId(), m16.getName(), bTeam.getId(), bTeam.getName(), 26, E.getId(), E.getName(), m16.isCharge()));
        m16cell.put(27, new DraftCell(m16.getId(), m16.getName(), bTeam.getId(), bTeam.getName(), 27, off.getId(), off.getName(), m16.isCharge()));
        m16cell.put(28, new DraftCell(m16.getId(), m16.getName(), bTeam.getId(), bTeam.getName(), 28, off.getId(), off.getName(), m16.isCharge()));
        m16cell.put(29, new DraftCell(m16.getId(), m16.getName(), bTeam.getId(), bTeam.getName(), 29, E.getId(), E.getName(), m16.isCharge()));
        m16cell.put(30, new DraftCell(m16.getId(), m16.getName(), bTeam.getId(), bTeam.getName(), 30, E.getId(), E.getName(), m16.isCharge()));
        m16cell.put(31, new DraftCell(m16.getId(), m16.getName(), bTeam.getId(), bTeam.getName(), 31, off.getId(), off.getName(), m16.isCharge()));
        cells.put(m16.getId(), m16cell);
        requestRepository.save(new Request(m16, off, LocalDate.of(2025, 9, 21), LocalDate.of(2025, 9, 22)));
        allowedShiftRepository.save(new MemberAllowedShift(m16, D));
        allowedShiftRepository.save(new MemberAllowedShift(m16, E));
        allowedShiftRepository.save(new MemberAllowedShift(m16, N));
        allowedShiftRepository.save(new MemberAllowedShift(m16, off));


        Member m17 = memberRepository.save(new Member("이지은", "이지은", "01011112218", "1234"));
        m17.joinWard(ward, bTeam, grade3);
        Map<Integer, DraftCell> m17cell = new HashMap<>();
        m17cell.put(25, new DraftCell(m17.getId(), m17.getName(), bTeam.getId(), bTeam.getName(), 25, D.getId(), D.getName(), m17.isCharge()));
        m17cell.put(26, new DraftCell(m17.getId(), m17.getName(), bTeam.getId(), bTeam.getName(), 26, E.getId(), E.getName(), m17.isCharge()));
        m17cell.put(27, new DraftCell(m17.getId(), m17.getName(), bTeam.getId(), bTeam.getName(), 27, E.getId(), E.getName(), m17.isCharge()));
        m17cell.put(28, new DraftCell(m17.getId(), m17.getName(), bTeam.getId(), bTeam.getName(), 28, E.getId(), E.getName(), m17.isCharge()));
        m17cell.put(29, new DraftCell(m17.getId(), m17.getName(), bTeam.getId(), bTeam.getName(), 29, E.getId(), E.getName(), m17.isCharge()));
        m17cell.put(30, new DraftCell(m17.getId(), m17.getName(), bTeam.getId(), bTeam.getName(), 30, E.getId(), E.getName(), m17.isCharge()));
        m17cell.put(31, new DraftCell(m17.getId(), m17.getName(), bTeam.getId(), bTeam.getName(), 31, off.getId(), off.getName(), m17.isCharge()));
        cells.put(m17.getId(), m17cell);
        requestRepository.save(new Request(m17, off, LocalDate.of(2025, 9, 20), LocalDate.of(2025, 9, 20)));
        allowedShiftRepository.save(new MemberAllowedShift(m17, D));
        allowedShiftRepository.save(new MemberAllowedShift(m17, E));
        allowedShiftRepository.save(new MemberAllowedShift(m17, N));
        allowedShiftRepository.save(new MemberAllowedShift(m17, off));


        Member m18 = memberRepository.save(new Member("권영화", "권영화", "01011112219", "1234"));
        m18.joinWard(ward, bTeam, grade3);
        Map<Integer, DraftCell> m18cell = new HashMap<>();
        m18cell.put(25, new DraftCell(m18.getId(), m18.getName(), bTeam.getId(), bTeam.getName(), 25, off.getId(), off.getName(), m18.isCharge()));
        m18cell.put(26, new DraftCell(m18.getId(), m18.getName(), bTeam.getId(), bTeam.getName(), 26, off.getId(), off.getName(), m18.isCharge()));
        m18cell.put(27, new DraftCell(m18.getId(), m18.getName(), bTeam.getId(), bTeam.getName(), 27, N.getId(), N.getName(), m18.isCharge()));
        m18cell.put(28, new DraftCell(m18.getId(), m18.getName(), bTeam.getId(), bTeam.getName(), 28, N.getId(), N.getName(), m18.isCharge()));
        m18cell.put(29, new DraftCell(m18.getId(), m18.getName(), bTeam.getId(), bTeam.getName(), 29, N.getId(), N.getName(), m18.isCharge()));
        m18cell.put(30, new DraftCell(m18.getId(), m18.getName(), bTeam.getId(), bTeam.getName(), 30, off.getId(), off.getName(), m18.isCharge()));
        m18cell.put(31, new DraftCell(m18.getId(), m18.getName(), bTeam.getId(), bTeam.getName(), 31, off.getId(), off.getName(), m18.isCharge()));
        cells.put(m18.getId(), m18cell);
        requestRepository.save(new Request(m18, off, LocalDate.of(2025, 9, 19), LocalDate.of(2025, 9, 22)));
        allowedShiftRepository.save(new MemberAllowedShift(m18, D));
        allowedShiftRepository.save(new MemberAllowedShift(m18, E));
        allowedShiftRepository.save(new MemberAllowedShift(m18, N));
        allowedShiftRepository.save(new MemberAllowedShift(m18, off));


        Member m19 = memberRepository.save(new Member("전수지", "전수지", "01011112220", "1234"));
        m19.joinWard(ward, bTeam, grade4);
        Map<Integer, DraftCell> m19cell = new HashMap<>();
        m19cell.put(25, new DraftCell(m19.getId(), m19.getName(), bTeam.getId(), bTeam.getName(), 25, E.getId(), E.getName(), m19.isCharge()));
        m19cell.put(26, new DraftCell(m19.getId(), m19.getName(), bTeam.getId(), bTeam.getName(), 26, E.getId(), E.getName(), m19.isCharge()));
        m19cell.put(27, new DraftCell(m19.getId(), m19.getName(), bTeam.getId(), bTeam.getName(), 27, E.getId(), E.getName(), m19.isCharge()));
        m19cell.put(28, new DraftCell(m19.getId(), m19.getName(), bTeam.getId(), bTeam.getName(), 28, E.getId(), E.getName(), m19.isCharge()));
        m19cell.put(29, new DraftCell(m19.getId(), m19.getName(), bTeam.getId(), bTeam.getName(), 29, off.getId(), off.getName(), m19.isCharge()));
        m19cell.put(30, new DraftCell(m19.getId(), m19.getName(), bTeam.getId(), bTeam.getName(), 30, off.getId(), off.getName(), m19.isCharge()));
        m19cell.put(31, new DraftCell(m19.getId(), m19.getName(), bTeam.getId(), bTeam.getName(), 31, E.getId(), E.getName(), m19.isCharge()));
        cells.put(m19.getId(), m19cell);
        allowedShiftRepository.save(new MemberAllowedShift(m19, D));
        allowedShiftRepository.save(new MemberAllowedShift(m19, E));
        allowedShiftRepository.save(new MemberAllowedShift(m19, N));
        allowedShiftRepository.save(new MemberAllowedShift(m19, off));

        DraftPayload payload = new DraftPayload(cells);
        backfillService.applyInitialHistory(schedule.getId(), payload);

        ruleRepository.save(new RequirementRule(aTeam, DayType.WEEKDAY, D, 5));
        ruleRepository.save(new RequirementRule(aTeam, DayType.WEEKDAY, E, 4));
        ruleRepository.save(new RequirementRule(aTeam, DayType.WEEKDAY, N, 3));
        ruleRepository.save(new RequirementRule(aTeam, DayType.WEEKEND, D, 3));
        ruleRepository.save(new RequirementRule(aTeam, DayType.WEEKEND, E, 3));
        ruleRepository.save(new RequirementRule(aTeam, DayType.WEEKEND, N, 2));
        ruleRepository.save(new RequirementRule(bTeam, DayType.WEEKDAY, D, 5));
        ruleRepository.save(new RequirementRule(bTeam, DayType.WEEKDAY, E, 4));
        ruleRepository.save(new RequirementRule(bTeam, DayType.WEEKDAY, N, 3));
        ruleRepository.save(new RequirementRule(bTeam, DayType.WEEKEND, D, 3));
        ruleRepository.save(new RequirementRule(bTeam, DayType.WEEKEND, E, 3));
        ruleRepository.save(new RequirementRule(bTeam, DayType.WEEKEND, N, 2));
        return ward;
    }

    @Test
    @DisplayName("")
    void candidate_생성_확인() {
        Ward ward = getSettedWard();
        Schedule schedule = scheduleRepository.save(new Schedule(ward, "2025-09"));
        draftService.getDraft(schedule.getId());
        Assertions.assertDoesNotThrow(() -> generatorService.generateCandidates(ward.getId(), schedule.getId()));
    }
}
