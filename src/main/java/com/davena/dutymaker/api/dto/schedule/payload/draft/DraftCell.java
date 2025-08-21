package com.davena.dutymaker.api.dto.schedule.payload.draft;

public record DraftCell(
        Long memberId,
        String memberName,
        Long teamId,
        String teamName,
        int day,
        Long shiftId,
        String shiftType,
        boolean isCharge
) {
}
