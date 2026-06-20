package com.notifplatform.notificationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NotificationEvent {
    private UUID notificationId;
    private String userId;                      // externalId from user
    private String type;                        // ORDER_SHIPPED, PASSWORD_RESET, etc
    private Map<String, Object> data;           // flexible payload
    private Instant createdAt;
}
