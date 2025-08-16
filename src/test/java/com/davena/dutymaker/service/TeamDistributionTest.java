package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.TeamBox;
import com.davena.dutymaker.api.dto.TeamDistributionRequest;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
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
        TeamDistributionService.class,
        MemberService.class,
        WardService.class,
        TeamService.class
})
public class TeamDistributionTest {

    @Autowired
    EntityManager em;

    @Autowired
    private TeamDistributionService teamDistributionService;
    @Autowired
    private WardService wardService;

    /// /////////해피 테스트
    @Test
    @DisplayName("병동의 팀 정보 업데이트하기")
    void 병동의_팀_정보_업데이트하기() {
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

        TeamBox teamBox1 = new TeamBox(null, "a팀", List.of(member1.getId(), member2.getId()));
        TeamBox teamBox2 = new TeamBox(null, "b팀", List.of(member3.getId()));
        TeamDistributionRequest request = new TeamDistributionRequest(List.of(teamBox1, teamBox2));
        teamDistributionService.updateTeamDistribution(ward.getId(), request);
        Assertions.assertEquals("b팀", member3.getTeam().getName());
    }

    @Test
    @DisplayName("병동의 팀이 기존 3개 -> 2개로 변경될 때, 병동 팀의 개수는 2개가 되어야 한다.")
    void 병동_팀_갯수_확인() {
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

        TeamBox teamBox1 = new TeamBox(null, "a팀", List.of(member1.getId()));
        TeamBox teamBox2 = new TeamBox(null, "b팀", List.of(member3.getId()));
        TeamBox teamBox3 = new TeamBox(null, "c팀", List.of(member2.getId()));
        TeamDistributionRequest request = new TeamDistributionRequest(List.of(teamBox1, teamBox2, teamBox3));
        teamDistributionService.updateTeamDistribution(ward.getId(), request);

        TeamBox teamBox4 = new TeamBox(null, "a팀", List.of(member1.getId(), member2.getId()));
        TeamBox teamBox5 = new TeamBox(null, "b팀", List.of(member3.getId()));
        TeamDistributionRequest updateTeam = new TeamDistributionRequest(List.of(teamBox4, teamBox5));
        teamDistributionService.updateTeamDistribution(ward.getId(), updateTeam);
        Ward reloaded = wardService.getWardWithMembers(ward.getId());
        Assertions.assertEquals(reloaded.getTeams().size(), 2);

    }

    /// ////////예외 테스트
    @Test
    @DisplayName("병동의 멤버들의 수와 팀의 멤버 수가 일치하지 않으면 예외 발생")
    void 병동_멤버_수와_팀_멤버_수_비교() {
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

        TeamBox teamBox1 = new TeamBox(null, "a팀", List.of(member1.getId()));
        TeamBox teamBox2 = new TeamBox(null, "b팀", List.of(member3.getId()));
        TeamDistributionRequest request = new TeamDistributionRequest(List.of(teamBox1, teamBox2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            teamDistributionService.updateTeamDistribution(ward.getId(), request);
        });
    }
}
