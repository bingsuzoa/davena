package com.davena.dutymaker.domain.organization.team;

import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.TeamRepository;
import com.davena.dutymaker.repository.WardRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class TeamTest {

    @Autowired
    EntityManager em;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Test
    @DisplayName("같은 병동에서 같은 이름의 Team 생성 불가 확인 테스트")
    void 같은_group_같은_이름_team_생성_불가() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));
        teamRepository.save(new Team(ward, "a팀"));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            teamRepository.save(new Team(ward, "a팀"));
        });
    }
}
