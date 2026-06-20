package com.notifplatform.notificationservice.dto;

import com.notifplatform.notificationservice.domain.enums.NotificationChannel;
import lombok.Data;

import java.time.LocalTime;

@Data
public class PreferenceResponse {
    private NotificationChannel channel;
    private boolean enabled;
    private LocalTime quietStart;
    private LocalTime quietEnd;
}
