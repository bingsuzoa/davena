package com.davena.common;

import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.davena.organization.domain.service.WardMembersService.NOT_SUPERVISOR;

@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository wardRepository;

    public static final String NOT_EXIST_WARD = "존재하지 않는 병동입니다.";

    public Ward getWard(UUID wardId) {
        return wardRepository.findById(wardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_WARD));
    }

    public boolean verifySupervisorOfWard(Ward ward, UUID supervisorId) {
        if (!ward.isSupervisor(supervisorId)) {
            throw new IllegalArgumentException(NOT_SUPERVISOR);
        }
        return true;
    }
}
