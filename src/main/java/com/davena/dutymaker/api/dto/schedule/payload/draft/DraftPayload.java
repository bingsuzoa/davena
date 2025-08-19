package com.davena.dutymaker.api.dto.schedule.payload.draft;

import java.util.HashMap;
import java.util.Map;

public record DraftPayload(
        Map<Long, Map<Integer, DraftCell>> board
) {

    public DraftPayload() {
        this(new HashMap<>());
    }
}
