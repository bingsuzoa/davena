package com.davena.constraint.domain.port;

import com.davena.constraint.domain.model.UnavailShiftRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnavailShiftRepository {

    UnavailShiftRequest save(UnavailShiftRequest request);

    UUID delete(UUID requestId);

    List<UnavailShiftRequest> findByWardIdAndYearAndMonth(UUID wardId, int year, int month);

    List<UnavailShiftRequest> findByMemberIdAndYearAndMonth(UUID memberId, int year, int month);

    Optional<UnavailShiftRequest> findByMemberIdAndShiftIdAndRequestDay(UUID memberId, UUID shiftId, LocalDate requestDay);

    List<UnavailShiftRequest> findByMemberIdAndRequestDay(UUID memberId, LocalDate requestDay);

}
