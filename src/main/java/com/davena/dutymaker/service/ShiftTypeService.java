package com.davena.dutymaker.service;

import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.ShiftTypeRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShiftTypeService {

    private final ShiftTypeRepository shiftRepository;
    private final WardRepository wardRepository;

    public ShiftType getShiftType(Long shiftTypeId) {
        Optional<ShiftType> optionalShift = shiftRepository.findById(shiftTypeId);
        if (optionalShift.isEmpty()) {
            throw new IllegalArgumentException(ShiftType.NOT_EXIST_SHIFT_TYPE);
        }
        return optionalShift.get();
    }

    public ShiftType createOffType(Ward ward) {
        return shiftRepository.save(new ShiftType(ward, ShiftType.OFF, null, null, false));
    }

    public Ward getWard(Long wardId) {
        return wardRepository.findById(wardId)
                .orElseThrow(() -> new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }
}
