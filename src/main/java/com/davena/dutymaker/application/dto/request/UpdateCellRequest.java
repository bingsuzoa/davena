package com.davena.dutymaker.application.dto.request;

import com.davena.dutymaker.application.dto.AssignmentDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpdateCellRequest(
        UUID wardId,
        UUID scheduleId,
        UUID candidateId,
        Map<Integer, List<AssignmentDto>> assignments
) {
}
