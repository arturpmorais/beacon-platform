package com.notifplatform.auditservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AuditRequest {

    @NotNull
    private UUID notificationId;

    @NotBlank
    private String userId;

    @NotBlank
    private String channel;

    @NotBlank
    private String status;

    private String errorMessage;
    private String providerResponse;
}
