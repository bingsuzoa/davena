package com.davena.dutymaker.api.dto.schedule.payload.draft;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public record DraftPayload(
        Map<Long, Map<Integer, DraftCell>> board
) {

    public DraftPayload() {
        this(new HashMap<>());
    }

    public Collection<DraftCell> getCells() {
        return board.values().stream()
                .flatMap(m -> m.values().stream())
                .toList();
    }
}
