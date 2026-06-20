package com.notifplatform.userservice.dto.response;

import com.notifplatform.userservice.domain.enums.NotificationChannel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class PreferenceResponse {
    private NotificationChannel channel;
    private boolean enabled;
    private LocalTime quietStart;
    private LocalTime quietEnd;
}
