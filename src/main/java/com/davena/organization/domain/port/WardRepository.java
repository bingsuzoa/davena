package com.davena.organization.domain.port;

import com.davena.organization.domain.model.ward.Ward;

public interface WardRepository {

    Ward save(Ward ward);
}
