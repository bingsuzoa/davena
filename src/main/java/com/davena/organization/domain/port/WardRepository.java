package com.davena.organization.domain.port;

import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.model.ward.WardId;

import java.util.Optional;

public interface WardRepository {

    Ward save(Ward ward);

    Optional<Ward> findByToken(String token);

    Optional<Ward> findById(WardId wardId);

}
