package com.davena.dutymaker.api.dto.schedule.payload.finalized;

import com.davena.dutymaker.api.dto.schedule.AssignmentDto;

import java.util.List;

public record FinalizedPayload(
        Long selectedCandidateId,
        List<AssignmentDto> assignments
) {
}
