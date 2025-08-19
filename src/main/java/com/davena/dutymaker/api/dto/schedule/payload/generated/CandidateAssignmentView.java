package com.davena.dutymaker.api.dto.schedule.payload.generated;

import com.davena.dutymaker.api.dto.schedule.AssignmentDto;

import java.util.List;

public record CandidateAssignmentView(
        Long candidateId,
        List<AssignmentDto> assignments
) {
}
