package com.davena.dutymaker.api.dto.schedule;

import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.api.dto.schedule.payload.finalized.FinalizedPayload;
import com.davena.dutymaker.api.dto.schedule.payload.generated.GeneratedPayload;

public record ScheduleView(
        Long scheduleId,
        String type,
        String wardName,
        String yearMonth,
        Long selectedCandidateId,
        Object payload
) {

    public static ScheduleView draft(Long scheduleId, String wardName, String yearMonth, DraftPayload payload) {
        return new ScheduleView(scheduleId, "draft", wardName, yearMonth, null, payload);
    }

    public static ScheduleView generated(Long scheduleId, String wardName, String yearMonth,
                                         Long selectedId, GeneratedPayload payload) {
        return new ScheduleView(scheduleId, "generated", wardName, yearMonth, selectedId, payload);
    }

    public static ScheduleView finalized(Long scheduleId, String wardName, String yearMonth,
                                         FinalizedPayload payload) {
        return new ScheduleView(scheduleId, "finalized", wardName, yearMonth, payload.selectedCandidateId(), payload);
    }
}
