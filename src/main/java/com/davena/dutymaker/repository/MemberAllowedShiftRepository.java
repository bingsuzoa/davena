package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.member.MemberAllowedShift;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAllowedShiftRepository extends JpaRepository<MemberAllowedShift, Long> {

    @Query("select m.shiftType from MemberAllowedShift m where m.member.id = :id")
    List<ShiftType> findShiftTypesByMemberId(@Param("id") Long memberId);
}
