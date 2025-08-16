package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.SkillGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillGradeRepository extends JpaRepository<SkillGrade, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from SkillGrade s where s.ward.id = :wardId")
    void deleteByWardId(Long wardId);
}
