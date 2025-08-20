package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    long countByWardId(Long wardId);

    List<Member> findByWardId(Long wardId);

    @Query("select m from Member m join fetch m.team where m.ward.id = :wardId")
    List<Member> findByWardIdWithTeam(@Param("wardId") Long wardId);
}
