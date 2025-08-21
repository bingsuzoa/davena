package com.davena.dutymaker.api.dto.member;

import java.util.List;
import java.util.Map;

public record MemberAllowedShiftRequest(
        Map<Long, List<Long>> allowedShifts
) {
}
