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

    List<UnavailShiftRequest> findByMemberIdAndWardIdAndYearAndMonth(UUID wardId, UUID memberId, int year, int month);

    Optional<UnavailShiftRequest> findByMemberIdAndWardIdAndShiftIdAndRequestDay(UUID wardId, UUID memberId, UUID shiftId, LocalDate requestDay);

}
