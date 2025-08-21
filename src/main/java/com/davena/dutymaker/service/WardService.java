package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.member.ChargeBox;
import com.davena.dutymaker.api.dto.member.ChargeRequest;
import com.davena.dutymaker.api.dto.ward.WardRequest;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.repository.HospitalRepository;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.davena.dutymaker.domain.organization.member.Member.MAX_RANKING;

@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository wardRepository;
    private final MemberRepository memberRepository;
    private final ShiftTypeService shiftService;
    private final HospitalRepository hospitalRepository;

    public ChargeRequest getMembersForCharge(Long wardId) {
        List<Member> members = memberRepository.findMembersWithTeamByWardId(wardId);
        Map<Long, ChargeBox> chargeRequest = new HashMap<>();

        for (Member member : members) {
            Team team = member.getTeam();
            chargeRequest.put(
                    member.getId(),
                    new ChargeBox(team.getId(), team.getName(), member.getName(), false, MAX_RANKING)
            );
        }
        return new ChargeRequest(chargeRequest);
    }

    public Ward createWardAndOffType(Long memberId, WardRequest wardRequest) {
        Hospital hospital = hospitalRepository.save(new Hospital());////////수정필요
        Member member = getMember(memberId);
        Ward ward = wardRepository.save(new Ward(hospital, member, wardRequest.name()));
        shiftService.createOffType(ward);
        return ward;
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException(Member.NOT_EXIST_MEMBER));
    }
}
