package com.davena.dutymaker.domain.port;

import com.davena.dutymaker.domain.model.schedule.Candidate;

import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository {

    Candidate saveCandidate(Candidate candidate);

    Optional<Candidate> findById(UUID candidateId);
}
