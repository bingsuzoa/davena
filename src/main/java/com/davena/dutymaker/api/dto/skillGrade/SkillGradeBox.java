package com.davena.dutymaker.api.dto.skillGrade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SkillGradeBox(
        Long skillGradeId,
        @NotBlank String name,
        @NotEmpty List<@NotNull Long> members
) {
}
