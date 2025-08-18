package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.ward.WardRequest;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository wardRepository;
    private final MemberRepository memberRepository;

    public Ward createWard(Long memberId, WardRequest wardRequest) {
        Hospital hospital = new Hospital();////////수정 필요!!!
        Member member = getMember(memberId);
        Ward ward = new Ward(hospital, member, wardRequest.name());
        return wardRepository.save(ward);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException(Member.NOT_EXIST_MEMBER));
    }

}
