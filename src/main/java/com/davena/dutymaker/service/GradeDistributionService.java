package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.skillGrade.GradeDistributionRequest;
import com.davena.dutymaker.api.dto.skillGrade.GradeUpdateRequest;
import com.davena.dutymaker.api.dto.skillGrade.SkillGradeBox;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.SkillGradeRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.davena.dutymaker.domain.organization.SkillGrade.NOT_MATCH_GRADE_WITH_WARD_MEMBERS_COUNT;

@Service
@RequiredArgsConstructor
public class GradeDistributionService {

    private final SkillGradeRepository skillGradeRepository;
    private final MemberRepository memberRepository;
    private final WardRepository wardRepository;

    @Transactional
    public void updateGrade(Long wardId, Long gradeId, GradeUpdateRequest request) {
        Optional<SkillGrade> optionalGrade = skillGradeRepository.findByWardIdAndId(wardId, gradeId);
        if(optionalGrade.isEmpty()) {
            throw new IllegalArgumentException(SkillGrade.NOT_EXIST_GRADE);
        }
        SkillGrade grade = optionalGrade.get();
        grade.updateName(request.name());
    }

    @Transactional
    public void deleteGrade(Long wardId, Long gradeId) {
        Optional<SkillGrade> optionalGrade = skillGradeRepository.findByWardIdAndId(wardId, gradeId);
        if(optionalGrade.isEmpty()) {
            throw new IllegalArgumentException(SkillGrade.NOT_EXIST_GRADE);
        }
        if(optionalGrade.get().isDefault()) {
            throw new IllegalArgumentException(SkillGrade.CANNOT_DELETE_DEFAULT_SKILL_GRADE);
        }
        SkillGrade grade = optionalGrade.get();
        SkillGrade defaultGrade = skillGradeRepository.findByWardIdAndIsDefaultTrue(wardId).get();
        skillGradeRepository.reassignMembers(grade, defaultGrade);
        skillGradeRepository.delete(grade);
    }

    public void createSkillGrades(Long wardId, GradeDistributionRequest request) {
        matchesWardMembersCount(wardId, request);
        Ward ward = getWard(wardId);
        for (SkillGradeBox grade : request.skillgrades()) {
            if(grade.skillGradeId() == null) {
                updateSkillGroupOfMember(createSkillGrade(ward, grade.name()), grade.members());
            } else {
                updateSkillGroupOfMember(getSkillGrade(grade.skillGradeId()), grade.members());
            }
        }
        deleteUnusedGrades(wardId);
    }

    private void matchesWardMembersCount(Long wardId, GradeDistributionRequest request) {
        Long totalMemberCount = memberRepository.countByWardId(wardId);
        List<SkillGradeBox> skillgrades = request.skillgrades();
        Long count = 0L;
        for (SkillGradeBox grade : skillgrades) {
            count += grade.members().size();
        }
        if (totalMemberCount != count) {
            throw new IllegalArgumentException(NOT_MATCH_GRADE_WITH_WARD_MEMBERS_COUNT);
        }
    }

    private Ward getWard(Long wardId) {
        return wardRepository.findById(wardId).orElseThrow(() -> new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }

    private SkillGrade createSkillGrade(Ward ward, String name) {
        return skillGradeRepository.save(new SkillGrade(ward, name));
    }

    private void updateSkillGroupOfMember(SkillGrade grade, List<Long> members) {
        for (Long id : members) {
            Member member = getMember(id);
            member.updateSkillGrade(grade);
        }
    }

    private void deleteUnusedGrades(Long wardId) {
        wardRepository.deleteEmptyGrades(wardId);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException(Member.NOT_EXIST_MEMBER));
    }

    private SkillGrade getSkillGrade(Long gradeId) {
        return skillGradeRepository.findById(gradeId).orElseThrow(() ->
                new IllegalArgumentException(SkillGrade.NOT_EXIST_GRADE));
    }
}
