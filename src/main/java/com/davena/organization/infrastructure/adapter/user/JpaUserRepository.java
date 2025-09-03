package com.davena.organization.infrastructure.adapter.user;

import com.davena.organization.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<User, UUID> {
}
