package com.notifplatform.auditservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditResponse {
    private UUID id;
    private UUID notificationId;
    private String userId;
    private String channel;
    private String status;
    private String errorMessage;
    private String providerResponse;
    private LocalDateTime createdAt;
}
