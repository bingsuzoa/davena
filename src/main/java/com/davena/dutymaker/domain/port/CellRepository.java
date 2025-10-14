package com.davena.dutymaker.domain.port;

import com.davena.dutymaker.domain.model.schedule.Cell;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CellRepository {

    Cell saveCell(Cell cell);

    List<Cell> findByCandidateId(UUID candidateId);

    Optional<Cell> findById(UUID cellId);
}
