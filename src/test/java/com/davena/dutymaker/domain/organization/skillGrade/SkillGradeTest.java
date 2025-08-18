package com.davena.dutymaker.domain.organization.skillGrade;

import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

//@DataJpaTest
//public class SkillGradeTest {
//
//    @Autowired
//    EntityManager em;
//
//    @Test
//    @DisplayName("같은 병동에서 Skill Grade 이름은 중복일 수 없다.")
//    void 같은_병동에서_Skill_grade_이름_중복_불가() {
//        Hospital hospital = new Hospital();
//        em.persist(hospital);
//
//        Member supervisor = new Member("팀장");
//        em.persist(supervisor);
//
//        Ward ward = new Ward(hospital, supervisor, "외상 병동");
//        em.persist(ward);
//
//        SkillGrade grade1 = new SkillGrade(ward, "grade1");
//        em.persist(grade1);
//
//        Assertions.assertThrows(ConstraintViolationException.class, () -> {
//            em.persist(new SkillGrade(ward, "grade1"));
//        });
//    }
//}
