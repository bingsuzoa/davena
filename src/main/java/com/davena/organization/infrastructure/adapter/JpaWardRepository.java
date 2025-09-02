package com.davena.organization.infrastructure.adapter;

import com.davena.organization.infrastructure.entity.WardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaWardRepository extends JpaRepository<WardEntity, UUID> {
}
