package com.davena.dutymaker.api.dto.schedule;

import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.api.dto.schedule.payload.finalized.FinalizedPayload;
import com.davena.dutymaker.api.dto.schedule.payload.generated.GeneratedPayload;
import com.davena.dutymaker.domain.schedule.ScheduleStatus;

public record ScheduleView(
        Long scheduleId,
        String type,
        String wardName,
        String yearMonth,
        Long selectedCandidateId,
        Object payload
) {

    public static ScheduleView draft(Long scheduleId, String wardName, String yearMonth, DraftPayload payload) {
        return new ScheduleView(scheduleId, ScheduleStatus.DRAFT.name(), wardName, yearMonth, null, payload);
    }

    public static ScheduleView generated(Long scheduleId, String wardName, String yearMonth,
                                         Long selectedId, GeneratedPayload payload) {
        return new ScheduleView(scheduleId, ScheduleStatus.GENERATED.name(), wardName, yearMonth, selectedId, payload);
    }

    public static ScheduleView finalized(Long scheduleId, String wardName, String yearMonth,
                                         FinalizedPayload payload) {
        return new ScheduleView(scheduleId, ScheduleStatus.FINALIZED.name(), wardName, yearMonth, payload.selectedCandidateId(), payload);
    }
}
