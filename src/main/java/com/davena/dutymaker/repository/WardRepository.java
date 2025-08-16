package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {

    @Query("select distinct w from Ward w left join fetch w.members where w.id = :wardId")
    Optional<Ward> getWardWithMembers(@Param("wardId") Long wardId);
}
