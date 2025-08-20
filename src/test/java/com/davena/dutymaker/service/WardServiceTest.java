package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.member.ChargeRequest;
import com.davena.dutymaker.api.dto.ward.WardRequest;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
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

@DataJpaTest
@Import({
        WardService.class,
        ShiftTypeService.class
})
@ActiveProfiles("test")
public class WardServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ShiftTypeRepository shiftRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private SkillGradeRepository gradeRepository;
    @Autowired
    private WardService wardService;

    @Test
    @DisplayName("병동이 생성될 때 기본 OFF ShiftType 객체 생성되는지 확인")
    void 병동_생성_시_기본_OFF_생성_확인() {
        Hospital hospital = hospitalRepository.save(new Hospital());
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        WardRequest wardRequest = new WardRequest(hospital.getId(), "외상 병동");
        Ward ward = wardService.createWardAndOffType(supervisor.getId(), wardRequest);
        Assertions.assertNotNull(shiftRepository.findByWardIdAndName(ward.getId(), ShiftType.OFF));
    }

    @Test
    @DisplayName("차지 여부 정하기 위해 멤버들 리스트 얻는 테스트")
    void 차지_여부_결정_위한_멤버들_리스트_얻는_테스트() {
        Hospital hospital = hospitalRepository.save(new Hospital());
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();
        SkillGrade defaultGrade = gradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).orElseThrow();

        Team bTeam = teamRepository.save(new Team(ward, "b팀"));

        Member member1 = memberRepository.save(new Member("박간호", "박간호", "01011112223", "1234"));
        member1.joinWard(ward, defaultTeam, defaultGrade);

        Member member2 = memberRepository.save(new Member("최간호", "최간호", "01011112224", "1234"));
        member2.joinWard(ward, bTeam, defaultGrade);

        Member member3 = memberRepository.save(new Member("유간호", "유간호", "01011112225", "1234"));
        member3.joinWard(ward, bTeam, defaultGrade);

        ChargeRequest chargeRequest = wardService.getMembersForCharge(ward.getId());
        Assertions.assertEquals(chargeRequest.chargeMap().size(), 3);
        Assertions.assertEquals(chargeRequest.chargeMap().get(member1.getId()).isCharge(), false);
        Assertions.assertEquals(chargeRequest.chargeMap().get(member1.getId()).ranking(), Member.MAX_RANKING);
    }
}
