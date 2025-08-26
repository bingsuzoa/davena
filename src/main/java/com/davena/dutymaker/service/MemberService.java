package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.member.ChargeBox;
import com.davena.dutymaker.api.dto.member.ChargeRequest;
import com.davena.dutymaker.api.dto.member.MemberAllowedShiftRequest;
import com.davena.dutymaker.api.dto.member.MemberRequest;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.member.MemberAllowedShift;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.davena.dutymaker.domain.organization.Ward.NOT_EXIST_DEFAULT_GRADE;
import static com.davena.dutymaker.domain.organization.member.Member.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final WardRepository wardRepository;
    private final TeamRepository teamRepository;
    private final SkillGradeRepository skillGradeRepository;
    private final ShiftTypeRepository shiftTypeRepository;
    private final MemberAllowedShiftRepository allowedShiftRepository;

    public void updateAllowedShifts(MemberAllowedShiftRequest request) {
        for (Long memberId : request.allowedShifts().keySet()) {
            Member member = memberRepository.findById(memberId).orElseThrow();
            for (Long shiftId : request.allowedShifts().get(memberId)) {
                ShiftType shiftType = shiftTypeRepository.findById(shiftId).orElseThrow();
                allowedShiftRepository.save(new MemberAllowedShift(member, shiftType));
            }
        }
    }

    public void updateChargeOfMember(ChargeRequest chargeRequest) {
        Map<Long, ChargeBox> chargeMap = chargeRequest.chargeMap();

        for (Long memberId : chargeMap.keySet()) {
            Member member = getMember(memberId);
            ChargeBox box = chargeMap.get(memberId);

            if (box.isCharge() && (box.ranking() < MIN_RANKING || box.ranking() > MAX_RANKING)) {
                throw new IllegalArgumentException(IS_CHARGE_IMPOSSIBLE_NUMBER);
            }
            if (box.isCharge()) {
                member.isCharge(box.isCharge(), box.ranking());
            }
        }
    }

    public Member createMember(MemberRequest memberRequest) {
        return memberRepository.save(
                new Member(
                        memberRequest.name(),
                        memberRequest.nickName(),
                        memberRequest.phoneNumber(),
                        memberRequest.password()
                ));
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
