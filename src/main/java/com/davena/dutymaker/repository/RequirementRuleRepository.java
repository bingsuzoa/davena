package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequirementRuleRepository extends JpaRepository<RequirementRule, Long> {

    List<RequirementRule> findByTeamId(Long teamId);

    List<RequirementRule> findByTeamIdAndDayType(Long TeamId, DayType dayType);
}
