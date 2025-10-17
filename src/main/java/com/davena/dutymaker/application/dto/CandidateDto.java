package com.davena.dutymaker.application.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CandidateDto(
        UUID candidateId,
        Map<Integer, List<AssignmentDto>> dailyAssignments
) {
}
