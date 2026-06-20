package com.notifplatform.auditservice.repository;

import com.notifplatform.auditservice.domain.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    List<NotificationLog> findByNotificationIdOrderByCreatedAtDesc(UUID notificationId);

    List<NotificationLog> findByUserIdOrderByCreatedAtDesc(String userId);
}
