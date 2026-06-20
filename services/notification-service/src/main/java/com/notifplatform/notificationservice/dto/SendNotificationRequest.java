package com.notifplatform.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class SendNotificationRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String type;            // ORDER_SHIPPED, PASSWORD_RESET, etc.

    @NotNull
    private Map<String, Object> data;
}
