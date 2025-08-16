package com.davena.dutymaker.service;

import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.repository.SkillGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillGradeService {

    private final SkillGradeRepository skillGradeRepository;

    public void deleteSkillGradeOfWard(Long wardId) {
        skillGradeRepository.deleteByWardId(wardId);
    }

    public SkillGrade createSkillGrade(SkillGrade skillGrade) {
        return skillGradeRepository.save(skillGrade);
    }
}
