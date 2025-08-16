package com.davena.dutymaker.repository;

import com.davena.dutymaker.domain.organization.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    long countByWardId(Long wardId);

}
