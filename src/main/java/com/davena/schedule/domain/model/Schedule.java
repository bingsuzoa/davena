package com.davena.schedule.domain.model;

import com.davena.schedule.domain.model.canididate.Candidate;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Schedule {

    public Schedule(UUID wardId, YearMonth yearMonth) {
        this.id = UUID.randomUUID();
        this.wardId = wardId;
        this.yearMonth = yearMonth;
    }

    private UUID id;

    private UUID wardId;

    private YearMonth yearMonth;

    private UUID finalizedCandidateId;

    private List<Candidate> candidates = new ArrayList<>();

    public void finalizeStatus(UUID candidateId) {
        this.finalizedCandidateId = candidateId;
    }
}
