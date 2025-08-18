package com.davena.dutymaker.service;

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

@DataJpaTest
@Import({
        MemberService.class
})
public class MemberServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private SkillGradeRepository skillGradeRepository;
    @Autowired
    private MemberService memberService;


    Member initMember() {
        return memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
    }

    Ward initWard() {
        Hospital hospital = new Hospital();
        em.persist(hospital);
        Member supervisor = memberRepository.save(new Member("팀장", "김간호", "01011112223", "1234"));
        return wardRepository.save(new Ward(hospital, supervisor, "외상병동"));
    }

    @Test
    @DisplayName("Member가 Ward 소속될 때 테스트")
    void Member가_ward_소속될_때_테스트() {
        Member member = initMember();
        Ward defaultWard = initWard();
        memberService.saveWardOfMember(defaultWard.getId(), member.getId());
        Member updatedMember = memberRepository.findById(member.getId()).get();
        Assertions.assertEquals(updatedMember.getWard().getName(), "외상병동");
        Assertions.assertEquals(updatedMember.getTeam().getName(), Team.DEFAULT_TEAM_NAME);
        Assertions.assertEquals(updatedMember.getSkillGrade().getName(), SkillGrade.DEFAULT_SKILL_GRADE);
    }
}
