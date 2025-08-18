package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.skillGrade.GradeDistributionRequest;
import com.davena.dutymaker.api.dto.skillGrade.GradeUpdateRequest;
import com.davena.dutymaker.api.dto.skillGrade.SkillGradeBox;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.SkillGradeRepository;
import com.davena.dutymaker.repository.TeamRepository;
import com.davena.dutymaker.repository.WardRepository;
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
        GradeDistributionService.class
})
public class GradeDistributionServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    private SkillGradeRepository skillGradeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private GradeDistributionService gradeDistributionService;

    @Test
    @DisplayName("병동 숙련도 이름 변경하는 테스트")
    void 병동_숙련도_이름_변경() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Member supervisor = memberRepository.save(new Member("팀장", "팀장", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = skillGradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member1 = memberRepository.save(new Member("김간호", "김간호", "01011112223", "1234"));
        member1.joinWard(ward, defaultTeam, defaultGrade);
        Member member2 = memberRepository.save(new Member("박간호", "박간호", "01011112224", "1234"));
        member2.joinWard(ward, defaultTeam, defaultGrade);
        Member member3 = memberRepository.save(new Member("최간호", "최간호", "01011112225", "1234"));
        member3.joinWard(ward, defaultTeam, defaultGrade);

        SkillGradeBox grade1 = new SkillGradeBox(defaultGrade.getId(), defaultGrade.getName(), List.of(member1.getId()));
        SkillGradeBox grade2 = new SkillGradeBox(null, "2등급", List.of(member2.getId(), member3.getId()));
        GradeDistributionRequest request = new GradeDistributionRequest(List.of(grade1, grade2));
        gradeDistributionService.createSkillGrades(ward.getId(), request);

        GradeUpdateRequest updatedName = new GradeUpdateRequest("업데이트 등급");
        gradeDistributionService.updateGrade(ward.getId(), defaultGrade.getId(), updatedName);

        Member updatedMember = memberRepository.findById(member1.getId()).get();
        Assertions.assertEquals(updatedMember.getSkillGrade().getName(), "업데이트 등급");
    }

    @Test
    @DisplayName("멤버의 숙련도 생성하는 테스트")
    void 멤버의_숙련도_생성_확인() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Member supervisor = memberRepository.save(new Member("팀장", "팀장", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = skillGradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member1 = memberRepository.save(new Member("김간호", "김간호", "01011112223", "1234"));
        member1.joinWard(ward, defaultTeam, defaultGrade);
        Member member2 = memberRepository.save(new Member("박간호", "박간호", "01011112224", "1234"));
        member2.joinWard(ward, defaultTeam, defaultGrade);
        Member member3 = memberRepository.save(new Member("최간호", "최간호", "01011112225", "1234"));
        member3.joinWard(ward, defaultTeam, defaultGrade);

        SkillGradeBox grade1 = new SkillGradeBox(defaultGrade.getId(), defaultGrade.getName(), List.of(member1.getId()));
        SkillGradeBox grade2 = new SkillGradeBox(null, "2등급", List.of(member2.getId(), member3.getId()));
        GradeDistributionRequest request = new GradeDistributionRequest(List.of(grade1, grade2));
        gradeDistributionService.createSkillGrades(ward.getId(), request);

        Ward updatedWard = wardRepository.getWardWithSkillGrades(ward.getId()).get();
        Assertions.assertEquals(updatedWard.getSkillGrades().size(), 2);
    }

    @Test
    @DisplayName("숙련도 삭제하는 테스트 -> 멤버는 default 숙련도 등급으로 이동")
    void 숙련도_삭제() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Member supervisor = memberRepository.save(new Member("팀장", "팀장", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = skillGradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member1 = memberRepository.save(new Member("김간호", "김간호", "01011112223", "1234"));
        member1.joinWard(ward, defaultTeam, defaultGrade);
        Member member2 = memberRepository.save(new Member("박간호", "박간호", "01011112224", "1234"));
        member2.joinWard(ward, defaultTeam, defaultGrade);
        Member member3 = memberRepository.save(new Member("최간호", "최간호", "01011112225", "1234"));
        member3.joinWard(ward, defaultTeam, defaultGrade);

        SkillGradeBox grade1 = new SkillGradeBox(defaultGrade.getId(), defaultGrade.getName(), List.of(member1.getId()));
        SkillGradeBox grade2 = new SkillGradeBox(null, "2등급", List.of(member2.getId(), member3.getId()));
        GradeDistributionRequest request = new GradeDistributionRequest(List.of(grade1, grade2));
        gradeDistributionService.createSkillGrades(ward.getId(), request);

        SkillGrade deleteGrade = skillGradeRepository.findByWardIdAndName(ward.getId(), "2등급").get();
        gradeDistributionService.deleteGrade(ward.getId(), deleteGrade.getId());

        Ward updatedWard = wardRepository.getWardWithSkillGrades(ward.getId()).get();
        Member updatedMember = memberRepository.findById(member3.getId()).get();
        Assertions.assertEquals(1, updatedWard.getSkillGrades().size());
        Assertions.assertEquals(SkillGrade.DEFAULT_SKILL_GRADE, updatedMember.getSkillGrade().getName());
    }

    /// ///예외 테스트
    @Test
    @DisplayName("기본 숙련도 삭제 시 예외 발생")
    void 기본_숙련도_삭제_시_예외_발생() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Member supervisor = memberRepository.save(new Member("팀장", "팀장", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = skillGradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member1 = memberRepository.save(new Member("김간호", "김간호", "01011112223", "1234"));
        member1.joinWard(ward, defaultTeam, defaultGrade);
        Member member2 = memberRepository.save(new Member("박간호", "박간호", "01011112224", "1234"));
        member2.joinWard(ward, defaultTeam, defaultGrade);
        Member member3 = memberRepository.save(new Member("최간호", "최간호", "01011112225", "1234"));
        member3.joinWard(ward, defaultTeam, defaultGrade);

        SkillGradeBox grade1 = new SkillGradeBox(defaultGrade.getId(), defaultGrade.getName(), List.of(member1.getId()));
        SkillGradeBox grade2 = new SkillGradeBox(null, "2등급", List.of(member2.getId(), member3.getId()));
        GradeDistributionRequest request = new GradeDistributionRequest(List.of(grade1, grade2));
        gradeDistributionService.createSkillGrades(ward.getId(), request);

        SkillGrade deleteGrade = skillGradeRepository.findByWardIdAndName(ward.getId(), defaultGrade.getName()).get();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            gradeDistributionService.deleteGrade(ward.getId(), deleteGrade.getId());
        });
    }
}
