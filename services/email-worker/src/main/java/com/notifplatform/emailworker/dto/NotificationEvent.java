package com.notifplatform.emailworker.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

// exactly matches notification-service NotificationEvent
@Data
public class NotificationEvent {
    private UUID notificationId;
    private String userId;
    private String type;
    private Map<String, Object> data;
    private Instant createdAt;
}
