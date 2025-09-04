package com.davena.organization.application.dto.ward;

import com.davena.organization.domain.model.ward.GradeId;
import com.davena.organization.domain.model.ward.WardId;

import java.util.UUID;

public record GradeResponse(
        UUID wardId,
        UUID gradeId,
        String name
) {
    public static GradeResponse from(WardId wardId, GradeId gradeId, String name) {
        return new GradeResponse(wardId.id(), gradeId.id(), name);
    }
}
