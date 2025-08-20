package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    long countByWardId(Long wardId);

    List<Member> findByWardId(Long wardId);

    @Query("select m from Member m join fetch m.team where m.ward.id = :wardId")
    List<Member> findMembersWithTeam(@Param("wardId") Long wardId);

    @Query("select m from Member m join fetch m.team where m.team.id = :teamId")
    List<Member> findMembersByTeamId(@Param("teamId") Long teamId);
}
