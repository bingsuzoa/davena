package com.davena.dutymaker.api.dto.schedule.backfill;

import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;

public record BackfillGrid(
        Long scheduleId,
        DraftPayload payload
) {
}
