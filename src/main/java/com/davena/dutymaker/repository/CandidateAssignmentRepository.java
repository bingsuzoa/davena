package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.schedule.CandidateAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateAssignmentRepository extends JpaRepository<CandidateAssignment, Long> {
}
