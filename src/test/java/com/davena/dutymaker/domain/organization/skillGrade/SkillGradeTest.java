package com.davena.dutymaker.domain.organization.skillGrade;

import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.SkillGradeRepository;
import com.davena.dutymaker.repository.WardRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class SkillGradeTest {

    @Autowired
    EntityManager em;
    @Autowired
    private SkillGradeRepository gradeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WardRepository wardRepository;

    @Test
    @DisplayName("같은 병동에서 Skill Grade 이름은 중복일 수 없다.")
    void 같은_병동에서_Skill_grade_이름_중복_불가() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));

        gradeRepository.save(new SkillGrade(ward, "grade1"));

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            em.persist(new SkillGrade(ward, "grade1"));
        });
    }

    @Test
    @DisplayName("다른 병동에서 Skill Grade 이름 중복 가능.")
    void 다른_병동에서_Skill_grade_이름_중복_가능() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));
        gradeRepository.save(new SkillGrade(ward, "2등급"));

        Member otherSuperVisor = memberRepository.save(new Member("박간호", "박간호", "01011112223", "1234"));
        Ward otherWard = wardRepository.save(new Ward(hospital, otherSuperVisor, "301동"));
         gradeRepository.save(new SkillGrade(otherWard, "2등급"));
    }
}
