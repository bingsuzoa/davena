package com.davena.constraint.domain.port;

import com.davena.constraint.domain.model.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findByUserId(UUID userId);

    List<Member> findAllByWardId(UUID wardId);
}
