package com.notifplatform.userservice.repository;

import com.notifplatform.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);
}
