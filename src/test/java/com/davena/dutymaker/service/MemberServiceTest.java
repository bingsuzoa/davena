package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.member.ChargeBox;
import com.davena.dutymaker.api.dto.member.ChargeRequest;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Map;

@DataJpaTest
@Import({
        MemberService.class
})
@ActiveProfiles("test")
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
    private HospitalRepository hospitalRepository;
    @Autowired
    private SkillGradeRepository gradeRepository;
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

    /// //// 해피 테스트

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

        Map<Long, ChargeBox> chargeBox = new HashMap<>();
        chargeBox.put(member1.getId(), new ChargeBox(defaultTeam.getId(), defaultTeam.getName(), member1.getName(), true, 1));
        chargeBox.put(member2.getId(), new ChargeBox(bTeam.getId(), bTeam.getName(), member2.getName(), true, 1));
        ChargeRequest chargeRequest = new ChargeRequest(chargeBox);
        memberService.updateChargeOfMember(chargeRequest);

        Member chargeMember = memberRepository.findById(member1.getId()).orElseThrow();
        Assertions.assertTrue(chargeMember.isCharge());
        Assertions.assertEquals(chargeMember.getRank(), 1);
    }

    /// ///예외 테스트
    @Test
    @DisplayName("차지인데 Rank가 음수 이면 예외")
    void 차지인데_Rank_음수이면_예외() {
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

        Map<Long, ChargeBox> chargeBox = new HashMap<>();
        chargeBox.put(member1.getId(), new ChargeBox(defaultTeam.getId(), defaultTeam.getName(), member1.getName(), true, -1));
        chargeBox.put(member2.getId(), new ChargeBox(bTeam.getId(), bTeam.getName(), member2.getName(), true, 1));
        ChargeRequest chargeRequest = new ChargeRequest(chargeBox);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            memberService.updateChargeOfMember(chargeRequest);
        });
    }

    @Test
    @DisplayName("차지인데 Rank가 0 이면 예외")
    void 차지인데_Rank_0이면_예외() {
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

        Map<Long, ChargeBox> chargeBox = new HashMap<>();
        chargeBox.put(member1.getId(), new ChargeBox(defaultTeam.getId(), defaultTeam.getName(), member1.getName(), true, 0));
        chargeBox.put(member2.getId(), new ChargeBox(bTeam.getId(), bTeam.getName(), member2.getName(), true, 1));
        ChargeRequest chargeRequest = new ChargeRequest(chargeBox);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            memberService.updateChargeOfMember(chargeRequest);
        });
    }

    @Test
    @DisplayName("차지인데 Rank가 MAX보다 초과이면 예외")
    void 차지인데_Rank_MAX_초과이면_예외() {
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

        Map<Long, ChargeBox> chargeBox = new HashMap<>();
        chargeBox.put(member1.getId(), new ChargeBox(defaultTeam.getId(), defaultTeam.getName(), member1.getName(), true, Member.MAX_RANKING + 1));
        chargeBox.put(member2.getId(), new ChargeBox(bTeam.getId(), bTeam.getName(), member2.getName(), true, 1));
        ChargeRequest chargeRequest = new ChargeRequest(chargeBox);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            memberService.updateChargeOfMember(chargeRequest);
        });
    }
}
