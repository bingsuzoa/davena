package com.davena.organization.infrastructure.adapter;

import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import com.davena.organization.infrastructure.entity.WardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaWardRepositoryImpl implements WardRepository {

    private final JpaWardRepository wardRepository;

    @Override
    public Ward save(Ward ward) {
        WardEntity entity = WardEntity.from(ward);
        wardRepository.save(entity);
        return ward;
    }
}
