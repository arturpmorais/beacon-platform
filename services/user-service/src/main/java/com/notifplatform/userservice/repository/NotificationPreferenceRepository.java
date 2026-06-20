package com.notifplatform.userservice.repository;

import com.notifplatform.userservice.domain.entity.NotificationPreference;
import com.notifplatform.userservice.domain.enums.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {

    List<NotificationPreference> findAllByUserId(UUID userId);

    Optional<NotificationPreference> findByUserIdAndChannel(UUID userId, NotificationChannel channel);
}
