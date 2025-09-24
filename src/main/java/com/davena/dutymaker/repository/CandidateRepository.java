package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.schedule.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    int countByScheduleId(Long scheduleId);

    @Query("select c from Candidate c left join fetch c.schedule where c.id = :id")
    Optional<Candidate> findByIdWithSchedule(@Param("id") Long candidateId);

    boolean existsByScheduleIdAndSignature(Long scheduleId, String signature);
}
