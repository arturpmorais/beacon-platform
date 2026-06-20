package com.notifplatform.notificationservice.service;

import com.notifplatform.notificationservice.domain.enums.NotificationChannel;
import com.notifplatform.notificationservice.dto.PreferenceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient userServiceClient;

    public boolean isChannelEnabled(String userId, NotificationChannel channel) {
        try {
            PreferenceResponse pref = userServiceClient.get()
                    .uri("/users/{userId}/preferences/{channel}", userId, channel.name())
                    .retrieve()
                    .bodyToMono(PreferenceResponse.class)
                    .block();

            if (pref == null) return true;

            if (!pref.isEnabled()) {
                log.info("channel {} disabled for user {}, skipping", channel, userId);
                return false;
            }

            if (isQuietHour(pref)) {
                log.info("quiet hours active for user {} on channel {}, skipping", userId, channel);
                return false;
            }

            return true;

        } catch (Exception ex) {
            // defaults to true on error (fail open)
            log.warn("could not fetch preferences for user {}, defaulting to enabled: {}", userId, ex.getMessage());
            return true;
        }
    }

    private boolean isQuietHour(PreferenceResponse pref) {
        if (pref.getQuietStart() == null || pref.getQuietEnd() == null) return false;

        var now = java.time.LocalTime.now();
        var start = pref.getQuietStart();
        var end = pref.getQuietEnd();

        // handle overnight windows e.g. 22:00 -> 08:00
        if (start.isBefore(end)) {
            return now.isAfter(start) && now.isBefore(end);
        } else {
            return now.isAfter(start) || now.isBefore(end);
        }
    }
}
