package com.davena.dutymaker.domain.model.schedule;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Candidate {

    public Candidate(UUID scheduleId) {
        this.id = UUID.randomUUID();
        this.scheduleId = scheduleId;
    }

    private UUID id;
    private UUID scheduleId;
    private List<Cell> cells = new ArrayList<>();
    private double score;

    public void addCell(Cell cell) {
        cells.add(cell);
    }
}
