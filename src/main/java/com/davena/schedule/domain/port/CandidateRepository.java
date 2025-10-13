package com.davena.schedule.domain.port;

import com.davena.schedule.domain.model.canididate.Candidate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


public interface CandidateRepository {

    Optional<Candidate> findById(UUID candidateId);
}
