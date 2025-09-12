package com.davena.possibleShifts.domain.port;

import com.davena.possibleShifts.domain.model.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findByUserId(UUID userId);

    List<Member> findAllByWardId(UUID wardId);
}
