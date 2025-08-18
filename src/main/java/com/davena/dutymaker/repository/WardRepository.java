package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {

    @Query("select distinct w from Ward w left join fetch w.members where w.id = :wardId")
    Optional<Ward> getWardWithMembers(@Param("wardId") Long wardId);

    @Query("select distinct w from Ward w left join fetch w.teams where w.id = :wardId")
    Optional<Ward> getWardWithTeams(@Param("wardId") Long wardId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                delete from Team t
                 where t.ward.id = :wardId
                  and t.isDefault = false
                  and not exists (select m.id from Member m where m.team = t)
            """)
    void deleteEmptyTeams(@Param("wardId") Long wardId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                delete from SkillGrade s
                 where s.ward.id = :wardId
                  and s.isDefault = false
                  and not exists (select m.id from Member m where m.skillGrade = s)
            """)
    void deleteEmptyGrades(@Param("wardId") Long wardId);

    @Query("select distinct w from Ward w left join fetch w.shiftTypes where w.id = :wardId")
    Optional<Ward> getWardWithShiftTypes(@Param("wardId") Long wardId);

    @Query("select distinct w from Ward w left join fetch w.skillGrades where w.id = :wardId")
    Optional<Ward> getWardWithSkillGrades(@Param("wardId") Long wardId);

    @Query("select distinct w from Ward w left join fetch w.requirementRules where w.id = :wardId")
    Optional<Ward> getWardWithRequirementRules(@Param("wardId") Long wardId);
}
