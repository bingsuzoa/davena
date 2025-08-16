package com.davena.dutymaker.service;

import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final WardService wardService;

    public Long countMemberByWard(Long wardId) {
        return memberRepository.countByWardId(wardId);
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException(Member.NOT_EXIST_MEMBER));
    }

    public String saveWardOfMember(Long wardId, Long memberId) {
        Ward ward = wardService.getWard(wardId);
        Member member = getMember(memberId);
        return member.updateWard(ward);
    }
}
