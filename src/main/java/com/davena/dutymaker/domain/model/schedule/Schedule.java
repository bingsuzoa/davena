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
    private UUID finalizedCandidateId;
    private ScheduleStatus status = ScheduleStatus.PENDING;
    private List<Candidate> candidates = new ArrayList<>();

    public void finalizeStatus(UUID candidateId) {
        this.finalizedCandidateId = candidateId;
        status = ScheduleStatus.CONFIRMED;
    }

    public UUID getFinalizedCandidateId() {
        if(finalizedCandidateId == null) {
            throw new IllegalArgumentException(notFinalizedMessage(year));
        }
        return finalizedCandidateId;
    }

    public static String notFinalizedMessage(int year) {
        return year + "월의 스케줄이 아직 확정되지 않았습니다. 확정해주세요!";
    }

}
