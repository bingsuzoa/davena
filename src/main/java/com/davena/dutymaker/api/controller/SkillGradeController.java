package com.davena.dutymaker.api.controller;

import com.davena.dutymaker.api.dto.skillGrade.GradeDistributionRequest;
import com.davena.dutymaker.service.GradeDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skillGrades")
@RequiredArgsConstructor
public class SkillGradeController {

    private final GradeDistributionService gradeDistributionService;

    @PutMapping("/{wardId}/distribution")
    public void distributeSkillGrades(@PathVariable Long wardId, GradeDistributionRequest request) {
        gradeDistributionService.createSkillGrades(wardId, request);
    }
}
