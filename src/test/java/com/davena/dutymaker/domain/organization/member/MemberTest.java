package com.davena.dutymaker.domain.organization.member;

import com.davena.dutymaker.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Member 객체 생성하기")
    void member_객체_생성() {
        Member member = new Member("김간호", "김간호", "01011112222", "1234");
        Member savedMember = memberRepository.save(member);
        Assertions.assertEquals(savedMember.getName(), "김간호");
    }

    @Test
    @DisplayName("Member 객체 생성 시 Ward, Team, Skillgroup null 가능")
    void member_객체_생성시_Ward_Team_Skill_null_가능() {
        Member member = new Member("김간호", "김간호", "01011112222", "1234");
        Member savedMember = memberRepository.save(member);
        Assertions.assertNull(savedMember.getWard());
        Assertions.assertNull(savedMember.getTeam());
        Assertions.assertNull(savedMember.getSkillGrade());
    }

    /// ///예외 테스트
    @Test
    @DisplayName("휴대폰 번호가 동일한 경우 예외 발생")
    void 휴대폰_번호_동일한_경우_예외() {
        memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Member duplicated = new Member("박간호", "박간호", "01011112222", "1234");
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            memberRepository.save(duplicated);
        });
    }
}
