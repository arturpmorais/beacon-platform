package com.notifplatform.userservice.dto.request;

import com.notifplatform.userservice.domain.enums.NotificationChannel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdatePreferenceRequest {
    @NotNull
    private NotificationChannel channel;
    @NotNull
    private Boolean enabled;
    private LocalTime quietStart;
    private LocalTime quietEnd;
}
