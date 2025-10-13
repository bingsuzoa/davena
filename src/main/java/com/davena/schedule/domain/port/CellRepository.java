package com.davena.schedule.domain.port;

import com.davena.schedule.domain.model.canididate.Cell;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


public interface CellRepository {

    List<Cell> findByCandidateId(UUID candidateId);
}
