package com.notifplatform.emailworker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuditRequest {
    private UUID notificationId;
    private String userId;
    private String channel;             // always "EMAIL"
    private String status;              // SENT | FAILED | SKIPPED
    private String errorMessage;
    private String providerResponse;
}
