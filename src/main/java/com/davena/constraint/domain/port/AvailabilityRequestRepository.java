package com.davena.constraint.domain.port;

import com.davena.constraint.domain.model.AvailabilityRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityRequestRepository {

    AvailabilityRequest save(AvailabilityRequest request);

    List<AvailabilityRequest> getAllMembersRequest(UUID wardId, LocalDate month);
}
