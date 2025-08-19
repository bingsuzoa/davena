package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRuleRepository extends JpaRepository<RequirementRule, Long> {
}
