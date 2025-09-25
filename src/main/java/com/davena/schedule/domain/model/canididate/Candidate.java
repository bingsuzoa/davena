package com.davena.schedule.domain.model.canididate;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Candidate copy() {
        Candidate copy = new Candidate(this.scheduleId);
        copy.cells = new ArrayList<>(this.cells);
        copy.score = this.score;
        return copy;
    }
}
