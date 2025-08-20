package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Team t where t.ward.id = :wardId")
    void deleteByWardId(Long wardId);

    Optional<Team> findByWardIdAndIsDefaultTrue(Long wardId);

    Optional<Team> findByWardIdAndName(Long wardId, String name);

    Optional<Team> findByWardIdAndId(Long wardId, Long teamId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Member m set m.team = :to where m.team = :from")
    void reassignMembers(@Param("from") Team from, @Param("to") Team to);

}
