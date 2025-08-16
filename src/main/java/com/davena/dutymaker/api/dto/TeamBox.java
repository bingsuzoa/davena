package com.davena.dutymaker.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TeamBox(
        Long teamId,
        @NotBlank String name,
        @NotEmpty List<@NotNull Long> members
) {
}
