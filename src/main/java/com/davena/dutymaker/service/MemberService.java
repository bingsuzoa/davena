package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.member.MemberRequest;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.SkillGradeRepository;
import com.davena.dutymaker.repository.TeamRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.davena.dutymaker.domain.organization.Ward.NOT_EXIST_DEFAULT_GRADE;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final WardRepository wardRepository;
    private final TeamRepository teamRepository;
    private final SkillGradeRepository skillGradeRepository;

    public Member createMember(MemberRequest memberRequest) {
        return memberRepository.save(
                new Member(memberRequest.name(),
                        memberRequest.nickName(),
                        memberRequest.phoneNumber(),
                        memberRequest.password()));
    }

    public void saveWardOfMember(Long wardId, Long memberId) {
        Ward ward = getWard(wardId);
        Member member = getMember(memberId);

        Team defaultTeam = getDefaultTeam(wardId);
        SkillGrade defaultGrade = getDefaultGrade(wardId);
        member.joinWard(ward, defaultTeam, defaultGrade);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException(Member.NOT_EXIST_MEMBER));
    }

    private Ward getWard(Long wardId) {
        return wardRepository.findById(wardId).orElseThrow(() -> new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }

    private Team getDefaultTeam(Long wardId) {
        return teamRepository.findByWardIdAndIsDefaultTrue(wardId).orElseThrow(()
                -> new IllegalArgumentException(Ward.NOT_EXIST_DEFAULT_TEAM));
    }

    private SkillGrade getDefaultGrade(Long wardId) {
        return skillGradeRepository.findByWardIdAndIsDefaultTrue(wardId).orElseThrow(()
                -> new IllegalArgumentException(NOT_EXIST_DEFAULT_GRADE));
    }
}
