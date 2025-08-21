package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    long countByWardId(Long wardId);

    long countByTeamId(Long teamId);

    List<Member> findMembersByWardId(Long wardId);

    @Query("select m from Member m join fetch m.team where m.ward.id = :wardId")
    List<Member> findMembersWithTeamByWardId(@Param("wardId") Long wardId);

    @Query("select m from Member m join fetch m.team where m.team.id = :teamId")
    List<Member> findMembersWithTeamByTeamId(@Param("teamId") Long teamId);

    @Query("select m from Member m join fetch m.team where m.id = :id")
    Optional<Member> findMemberWithTeam(@Param("id") Long memberId);

    Optional<Member> findByPhoneNumber(String phoneNumber);
}
