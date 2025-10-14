package com.davena.dutymaker.domain.model.schedule;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Schedule {

    public Schedule(
            UUID wardId,
            int year,
            int month
    ) {
        this.id = UUID.randomUUID();
        this.wardId = wardId;
        this.year = year;
        this.month = month;
    }

    private UUID id;
    private UUID wardId;
    private int year;
    private int month;
    private ScheduleStatus status = ScheduleStatus.PENDING;
    private List<Candidate> candidates = new ArrayList<>();

    public static final String NOT_EXIST_CANDIDATE = "존재하지 않는 candidate";
    public static final String NOT_FINALIZED_MESSAGE = "아직 확정된 스케줄이 없습니다. 확정해주세요!";

    public void updateCell(UUID candidateId, UUID cellId, UUID shiftId) {
        for(Candidate candidate : candidates) {
            if(candidate.getId().equals(candidateId)) {
                candidate.updateCell(cellId, shiftId);
                return;
            }
        }
        throw new IllegalArgumentException(NOT_EXIST_CANDIDATE);
    }

    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
    }

    public void finalizeStatus() {
        status = ScheduleStatus.CONFIRMED;
    }

    public List<Cell> getCellsOfFinalizedCandidate() {
        for(Candidate candidate : candidates) {
            if(candidate.isFinalized()) {
                return candidate.getCells();
            }
        }
        throw new IllegalArgumentException(NOT_FINALIZED_MESSAGE);
    }

}
