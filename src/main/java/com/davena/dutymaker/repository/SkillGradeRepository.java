package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.SkillGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillGradeRepository extends JpaRepository<SkillGrade, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from SkillGrade s where s.ward.id = :wardId")
    void deleteByWardId(Long wardId);

    Optional<SkillGrade> findByWardIdAndIsDefaultTrue(Long wardId);

    Optional<SkillGrade> findByWardIdAndId(Long wardId, Long gradId);

    Optional<SkillGrade> findByWardIdAndName(Long wardId, String name);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Member m set m.skillGrade = :to where m.skillGrade = :from")
    void reassignMembers(@Param("from") SkillGrade from, @Param("to") SkillGrade to);
}
