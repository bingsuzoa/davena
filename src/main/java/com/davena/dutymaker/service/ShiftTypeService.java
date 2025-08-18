package com.davena.dutymaker.service;

import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.ShiftTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShiftTypeService {

    private final ShiftTypeRepository shiftRepository;

    public ShiftType getShiftType(Long shiftTypeId){
        Optional<ShiftType> optionalShift = shiftRepository.findById(shiftTypeId);
        if(optionalShift.isEmpty()) {
            throw new IllegalArgumentException(ShiftType.NOT_EXIST_SHIFT_TYPE);
        }
        return optionalShift.get();
    }
}
