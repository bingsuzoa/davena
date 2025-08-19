package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
                select distinct r
                from Request r
                join fetch r.member m
                join fetch r.shiftType st
                where m.ward.id = :wardId
                  and r.startDate <= :end
                  and r.endDate   >= :start
            """)
    List<Request> findRequestsByWardAndMonth(
            @Param("wardId") Long wardId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


}
