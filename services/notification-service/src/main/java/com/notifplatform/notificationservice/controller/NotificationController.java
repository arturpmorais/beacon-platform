package com.notifplatform.notificationservice.controller;

import com.notifplatform.notificationservice.dto.SendNotificationRequest;
import com.notifplatform.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, UUID> send(@Valid @RequestBody SendNotificationRequest request) {
        UUID notificationId = notificationService.dispatch(request);
        return Map.of("notificationId", notificationId);
    }
}
