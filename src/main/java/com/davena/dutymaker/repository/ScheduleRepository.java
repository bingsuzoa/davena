package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByWardIdAndYearMonth(Long wardId, String yearMonth);

    @Query("""
            select distinct s
            from Schedule s
            join fetch s.selectedCandidate sc
            join fetch sc.assignments a
            join fetch a.member m
            where s.id = :id
            """)
    Optional<Schedule> findWithSelectedWithAssignments(@Param("id") Long scheduleId);

    @Query("""
                select distinct s
                from Schedule s
                join fetch s.candidates c
                join fetch c.assignments a
                join fetch a.member m
                join fetch a.shiftType st
                where s.id = :id
            """)
    Optional<Schedule> findWithCandidatesAndAssignments(@Param("id") Long id);

    @Query("select s from Schedule s join fetch s.draft d where s.id = :id")
    Optional<Schedule> findByIdWithDraft(@Param("id") Long id);

    @Query("""
                select s
                from Schedule s
                join fetch s.selectedCandidate c
                where s.ward.id = :wardId
                and s.yearMonth = :yearMonth
            """)
    Optional<Schedule> findByWardAndYearMonthWithSelected(
            @Param("wardId") Long wardId,
            @Param("yearMonth") String yearMonth
    );

    @Query("select s from Schedule s left join fetch s.candidates where s.id = :id")
    Optional<Schedule> findWithCandidates(@Param("id") Long scheduleId);

}
