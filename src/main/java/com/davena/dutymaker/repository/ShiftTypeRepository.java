package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftTypeRepository extends JpaRepository<ShiftType, Long> {
}
