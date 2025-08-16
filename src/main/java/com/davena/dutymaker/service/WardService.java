package com.davena.dutymaker.service;

import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository wardRepository;

    public Ward getWard(Long wardId) {
        return wardRepository.findById(wardId).orElseThrow(() -> new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }

    public Ward getWardWithMembers(Long wardId) {
        return wardRepository.getWardWithMembers(wardId).orElseThrow(() -> new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }
}
