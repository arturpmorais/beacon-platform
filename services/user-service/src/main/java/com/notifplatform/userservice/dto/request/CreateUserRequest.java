package com.notifplatform.userservice.dto.request;

import com.notifplatform.userservice.domain.enums.NotificationChannel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateUserRequest {
    @NotNull
    private String externalId;
    private String email;
    private String phone;
    private String pushToken;
}
