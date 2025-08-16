package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Team t where t.ward.id = :wardId")
    void deleteByWardId(Long wardId);

}
