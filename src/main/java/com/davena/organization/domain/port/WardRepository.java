package com.davena.organization.domain.port;

import com.davena.organization.domain.model.ward.Ward;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


public interface WardRepository {

    Ward save(Ward ward);

    Optional<Ward> findByToken(String token);

    Optional<Ward> findById(UUID wardId);

}
