package com.davena.dutymaker.domain.model.schedule;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Cell implements Comparable<Cell> {

    public Cell(
            UUID id,
            UUID candidateId,
            UUID memberId,
            int workDay,
            UUID shiftId
    ) {
        this.id = id;
        this.candidateId = candidateId;
        this.memberId = memberId;
        this.workDay = workDay;
        this.shiftId = shiftId;
    }

    private UUID id;
    private UUID candidateId;
    private UUID memberId;
    private int workDay;
    private UUID shiftId;

    @Override
    public int compareTo(Cell other) {
        return Integer.compare(workDay, other.workDay);
    }

}
