package com.notifplatform.emailworker.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class PreferenceResponse {
    private String channel;
    private boolean enabled;
    private LocalTime quietStart;
    private LocalTime quietEnd;
}
