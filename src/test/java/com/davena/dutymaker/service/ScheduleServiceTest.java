package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.schedule.ScheduleView;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftCell;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.domain.Request;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.schedule.ScheduleStatus;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@DataJpaTest
@Import({
        ScheduleService.class,
        DraftService.class
})
@ActiveProfiles("test")
public class ScheduleServiceTest {

    @Autowired
    private EntityManager em;
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

    @Test
    @DisplayName("Schedule 초안 얻을 때 해당 월의 휴가 신청 정보를 모두 담은 객체 반환")
    void Schedule_초안_얻을_때_해당_월_휴가_신청_모두_담은_객체_반환() {
        Hospital hospital = new Hospital();
        em.persist(hospital);
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = gradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member = memberRepository.save(new Member("박간호", "박간호", "0101111222ㄷ", "1234"));
        member.joinWard(ward, defaultTeam, defaultGrade);
        ShiftType OFF = shiftRepository.save(new ShiftType(ward, ShiftType.OFF, null, null, false));

        requestRepository.save(new Request(member, OFF, LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 15)));
        ScheduleView view = scheduleService.getScheduleView(ward.getId(), YearMonth.of(2025, 9));

        Assertions.assertEquals(view.type(), ScheduleStatus.DRAFT.name());
        Map<Long, Map<Integer, DraftCell>> board = ((DraftPayload) view.payload()).board();
        Assertions.assertEquals(board.get(member.getId()).size(), 6);
    }
}
