package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.skillGrade.GradeDistributionRequest;
import com.davena.dutymaker.api.dto.skillGrade.SkillGradeBox;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@DataJpaTest
@Import({
        GradeDistributionService.class,
        SkillGradeService.class,
        MemberService.class,
        WardService.class
})
public class GradeDistributionServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private GradeDistributionService gradeDistributionService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버의 숙련도 생성하는 테스트")
    void 멤버의_숙련도_생성_확인() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Ward ward = new Ward(hospital, "외상 병동");
        em.persist(ward);

        Member member1 = new Member("김간호");
        em.persist(member1);
        member1.updateWard(ward);
        Member member2 = new Member("박간호");
        em.persist(member2);
        member2.updateWard(ward);
        Member member3 = new Member("최간호");
        em.persist(member3);
        member3.updateWard(ward);

        SkillGradeBox grade1 = new SkillGradeBox(null, "1등급", List.of(member1.getId()));
        SkillGradeBox grade2 = new SkillGradeBox(null, "2등급", List.of(member2.getId(), member3.getId()));
        GradeDistributionRequest request = new GradeDistributionRequest(List.of(grade1, grade2));
        gradeDistributionService.createSkillGrades(ward.getId(), request);

        Member member = memberRepository.findById(member2.getId()).orElseThrow();
        Assertions.assertEquals(member.getSkillGrade().getName(), "2등급");
    }
}
