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
    private boolean isFinalized = false;
    private List<Cell> cells = new ArrayList<>();
    private double score;

    public void addCell(Cell cell) {
        cells.add(cell);
    }

    public static final String NOT_EXIST_CELL = "존재하지 않는 cell";

    public void updateCell(UUID cellId, UUID shiftId) {
        for(Cell cell : cells) {
            if(cell.getId().equals(cellId)) {
                cell.updateShift(shiftId);
                return;
            }
        }
        throw new IllegalArgumentException(NOT_EXIST_CELL);
    }
}
