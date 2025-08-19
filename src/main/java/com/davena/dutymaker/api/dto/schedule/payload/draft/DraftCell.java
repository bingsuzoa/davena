package com.davena.dutymaker.api.dto.schedule.payload.draft;

public record DraftCell(
        Long memberId,
        String memberName,
        int day,
        Long shiftId,
        String shiftType
) {
}
