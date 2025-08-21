package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftTypeRepository extends JpaRepository<ShiftType, Long> {

    Optional<ShiftType> findByWardIdAndName(Long wardId, String name);

    @Query("select s from ShiftType s where s.ward.id = :id")
    List<ShiftType> findByWardId(@Param("id") Long wardId);

    Optional<ShiftType> findByWardIdAndIsWorkingFalse(Long wardId);
}
