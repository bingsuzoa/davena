package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.team.TeamBox;
import com.davena.dutymaker.api.dto.team.TeamDistributionRequest;
import com.davena.dutymaker.api.dto.team.TeamUpdateRequest;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({
        TeamDistributionService.class
})
public class TeamDistributionTest {

    @Autowired
    EntityManager em;

    @Autowired
    private TeamDistributionService teamDistributionService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private SkillGradeRepository gradeRepository;


    /// /////////해피 테스트
    @Test
    @DisplayName("팀 이름 변경하기")
    void 병동_팀_이름_변경() {
        Hospital hospital = new Hospital();
        em.persist(hospital);

        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        TeamUpdateRequest request = new TeamUpdateRequest("새로운 팀 이름");
        teamDistributionService.updateTeam(ward.getId(), defaultTeam.getId(), request);
        Team updatedTeam = teamRepository.findByWardIdAndId(ward.getId(), defaultTeam.getId()).get();
        Assertions.assertEquals(updatedTeam.getName(), "새로운 팀 이름");
    }
    @Test
    @DisplayName("병동의 팀 정보 삭제하기")
    void 병동의_팀_삭제() {
        Hospital hospital = new Hospital();
        em.persist(hospital);
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital,supervisor, "외상병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = gradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member1 = new Member("박간호", "박간호", "01011112223", "1234");;
        memberRepository.save(member1);
        member1.joinWard(ward, defaultTeam, defaultGrade);
        Member member2 = new Member("최간호", "최간호", "01011112224", "1234");
        memberRepository.save(member2);
        member2.joinWard(ward, defaultTeam, defaultGrade);
        Member member3 = new Member("유간호", "유간호", "01011112225", "1234");
        memberRepository.save(member3);
        member3.joinWard(ward, defaultTeam, defaultGrade);

        TeamBox teamBox1 = new TeamBox(defaultTeam.getId(), defaultTeam.getName(), List.of(member1.getId()));
        TeamBox teamBox2 = new TeamBox(null, "b팀", List.of(member2.getId(), member3.getId()));
        teamDistributionService.updateTeamDistribution(ward.getId(), new TeamDistributionRequest(List.of(teamBox1, teamBox2)));

        Team deleteTeam = teamRepository.findByWardIdAndName(ward.getId(), "b팀").get();
        teamDistributionService.deleteTeam(ward.getId(), deleteTeam.getId());
        em.flush();
        em.clear();
        Ward updatedWard = wardRepository.findById(ward.getId()).get();
        Assertions.assertEquals(1, updatedWard.getTeams().size());
    }

    @Test
    @DisplayName("병동의 팀 정보 업데이트하기")
    void 병동의_팀_정보_업데이트하기() {
        Hospital hospital = new Hospital();
        em.persist(hospital);
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital,supervisor, "외상병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = gradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member1 = new Member("박간호", "박간호", "01011112223", "1234");;
        memberRepository.save(member1);
        member1.joinWard(ward, defaultTeam, defaultGrade);
        Member member2 = new Member("최간호", "최간호", "01011112224", "1234");
        memberRepository.save(member2);
        member2.joinWard(ward, defaultTeam, defaultGrade);
        Member member3 = new Member("유간호", "유간호", "01011112225", "1234");
        memberRepository.save(member3);
        member3.joinWard(ward, defaultTeam, defaultGrade);

        TeamBox teamBox1 = new TeamBox(defaultTeam.getId(), defaultTeam.getName(), List.of(member1.getId()));
        TeamBox teamBox2 = new TeamBox(null, "b팀", List.of(member2.getId(), member3.getId()));
        TeamDistributionRequest request = new TeamDistributionRequest(List.of(teamBox1, teamBox2));
        teamDistributionService.updateTeamDistribution(ward.getId(), request);

        Ward updatedWard = wardRepository.getWardWithTeams(ward.getId()).get();
        Assertions.assertEquals(updatedWard.getTeams().size(), 2);
    }

    @Test
    @DisplayName("A팀, B팀, C팀 -> A팀, B팀으로 변경한 경우 C팀 delete")
    void 팀_변경_시_team_삭제_제대로_되는지_확인() {
        Hospital hospital = new Hospital();
        em.persist(hospital);
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital,supervisor, "외상병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = gradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member1 = new Member("박간호", "박간호", "01011112223", "1234");
        memberRepository.save(member1);
        member1.joinWard(ward, defaultTeam, defaultGrade);
        Member member2 = new Member("최간호", "최간호", "01011112224", "1234");
        memberRepository.save(member2);
        member2.joinWard(ward, defaultTeam, defaultGrade);
        Member member3 = new Member("유간호", "유간호", "01011112225", "1234");
        memberRepository.save(member3);
        member3.joinWard(ward, defaultTeam, defaultGrade);

        TeamBox teamBox1 = new TeamBox(defaultTeam.getId(), defaultTeam.getName(), List.of(member1.getId()));
        TeamBox teamBox2 = new TeamBox(null, "b팀", List.of(member2.getId()));
        TeamBox teamBox3 = new TeamBox(null, "c팀", List.of(member3.getId()));
        TeamDistributionRequest request = new TeamDistributionRequest(List.of(teamBox1, teamBox2, teamBox3));
        teamDistributionService.updateTeamDistribution(ward.getId(), request);

        Team bTeam = teamRepository.findByWardIdAndName(ward.getId(), "b팀").get();
        TeamBox teamBox4 = new TeamBox(defaultTeam.getId(), defaultTeam.getName(), List.of(member1.getId()));
        TeamBox teamBox5 = new TeamBox(bTeam.getId(), "b팀", List.of(member2.getId(), member3.getId()));
        TeamDistributionRequest request2 = new TeamDistributionRequest(List.of(teamBox4, teamBox5));
        teamDistributionService.updateTeamDistribution(ward.getId(), request2);

        assertTrue(teamRepository.findByWardIdAndName(ward.getId(), "c팀").isEmpty());
    }

    /// ///예외 테스트
    @Test
    @DisplayName("병동의 기본 팀 삭제 시도 시 예외 발생")
    void 병동_기본_팀_삭제_시_예외() {
        Hospital hospital = new Hospital();
        em.persist(hospital);
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital,supervisor, "외상병동"));

        Team defaultTeam = teamRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();
        SkillGrade defaultGrade = gradeRepository.findByWardIdAndIsDefaultTrue(ward.getId()).get();

        Member member1 = new Member("박간호", "박간호", "01011112223", "1234");;
        memberRepository.save(member1);
        member1.joinWard(ward, defaultTeam, defaultGrade);
        Member member2 = new Member("최간호", "최간호", "01011112224", "1234");
        memberRepository.save(member2);
        member2.joinWard(ward, defaultTeam, defaultGrade);
        Member member3 = new Member("유간호", "유간호", "01011112225", "1234");
        memberRepository.save(member3);
        member3.joinWard(ward, defaultTeam, defaultGrade);

        TeamBox teamBox1 = new TeamBox(defaultTeam.getId(), defaultTeam.getName(), List.of(member1.getId()));
        TeamBox teamBox2 = new TeamBox(null, "b팀", List.of(member2.getId(), member3.getId()));
        teamDistributionService.updateTeamDistribution(ward.getId(), new TeamDistributionRequest(List.of(teamBox1, teamBox2)));

        Team deleteTeam = teamRepository.findByWardIdAndName(ward.getId(), defaultTeam.getName()).get();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            teamDistributionService.deleteTeam(ward.getId(), deleteTeam.getId());
        });
    }
}
