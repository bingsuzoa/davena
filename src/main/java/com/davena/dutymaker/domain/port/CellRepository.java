package com.davena.dutymaker.domain.port;

import com.davena.dutymaker.domain.model.schedule.Cell;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CellRepository {

    List<Cell> findByCandidateId(UUID candidateId);
}
