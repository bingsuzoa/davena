package com.davena.constraint.domain.port;

import com.davena.constraint.domain.model.HolidayRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface HolidayRepository {

    HolidayRequest save(HolidayRequest request);

    UUID delete(UUID requestId);

    List<HolidayRequest> findByWardIdAndYearAndMonth(UUID wardId, int year, int month);

    List<HolidayRequest> findByMemberIdAndYearAndMonth(UUID memberId, int year, int month);

    Optional<HolidayRequest> findByMemberIdAndRequestDay(UUID memberId, LocalDate requestDay);
}
