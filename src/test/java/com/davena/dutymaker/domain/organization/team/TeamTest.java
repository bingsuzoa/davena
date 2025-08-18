package com.davena.dutymaker.domain.organization.team;

import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.Team;
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
//public class TeamTest {
//
//    @Autowired
//    EntityManager em;
//
//    @Test
//    @DisplayName("같은 Group에서 같은 이름의 Team 생성 불가 확인 테스트")
//    void 같은_group_같은_이름_team_생성_불가() {
//        Hospital hospital = new Hospital();
//        em.persist(hospital);
//
//        Member supervisor = new Member("팀장");
//        em.persist(supervisor);
//
//        Ward ward = new Ward(hospital, supervisor, "외상 병동");
//        em.persist(ward);
//
//        Team team = new Team(ward, "a팀");
//        em.persist(team);
//
//        Assertions.assertThrows(ConstraintViolationException.class, () -> {
//            new Team(ward, "a팀");
//            em.persist(new Team(ward, "a팀"));
//        });
//    }
//}
