package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.skillGrade.SkillGradeBox;
import com.davena.dutymaker.api.dto.skillGrade.GradeDistributionRequest;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.davena.dutymaker.domain.organization.SkillGrade.NOT_MATCH_GRADE_WITH_WARD_MEMBERS_COUNT;
import static com.davena.dutymaker.domain.organization.Team.NOT_MATCH_TEAM_WITH_WARD_MEMBERS_COUNT;

@Service
@RequiredArgsConstructor
public class GradeDistributionService {

    private final SkillGradeService skillGradeService;
    private final MemberService memberService;
    private final WardService wardService;

    public void createSkillGrades(Long wardId, GradeDistributionRequest request) {
        matchesWardMembersCount(wardId, request);
        resetWardSkillGrades(wardId);

        Ward ward = wardService.getWard(wardId);
        for(SkillGradeBox skillGrade : request.skillgrades()) {
            updateSkillGroupOfMember(ward, skillGrade);
        }
    }

    private void resetWardSkillGrades(Long wardId) {
        Set<Member> members = wardService.getWardWithMembers(wardId).getMembers();
        for (Member member : members) {
            member.initSkillGrade();
        }
        skillGradeService.deleteSkillGradeOfWard(wardId);
    }

    private void updateSkillGroupOfMember(Ward ward, SkillGradeBox newSkillGrade) {
        List<Long> members = newSkillGrade.members();

        for(Long id : members) {
            Member member = memberService.getMember(id);
            member.changeSkillGrade(new SkillGrade(ward, newSkillGrade.name()));
        }
    }

    private void matchesWardMembersCount(Long wardId, GradeDistributionRequest request) {
        Long totalMemberCount = memberService.countMemberByWard(wardId);
        List<SkillGradeBox> skillgrades = request.skillgrades();
        Long count = 0L;
        for (SkillGradeBox grade : skillgrades) {
            count += grade.members().size();
        }
        if (totalMemberCount != count) {
            throw new IllegalArgumentException(NOT_MATCH_GRADE_WITH_WARD_MEMBERS_COUNT);
        }
    }
}
