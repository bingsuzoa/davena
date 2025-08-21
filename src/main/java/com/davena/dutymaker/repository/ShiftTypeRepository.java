package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShiftTypeRepository extends JpaRepository<ShiftType, Long> {

    Optional<ShiftType> findByWardIdAndName(Long wardId, String name);
}
