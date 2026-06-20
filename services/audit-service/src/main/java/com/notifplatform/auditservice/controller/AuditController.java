package com.notifplatform.auditservice.controller;

import com.notifplatform.auditservice.dto.AuditRequest;
import com.notifplatform.auditservice.dto.AuditResponse;
import com.notifplatform.auditservice.service.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    // called by workers after each delivery attempt
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuditResponse log(@Valid @RequestBody AuditRequest request) {
        return auditService.save(request);
    }

    @GetMapping("/notifications/{notificationId}")
    public List<AuditResponse> byNotification(@PathVariable UUID notificationId) {
        return auditService.getByNotificationId(notificationId);
    }

    // full delivery history for a user
    @GetMapping("/users/{userId}")
    public List<AuditResponse> byUser(@PathVariable String userId) {
        return auditService.getByUserId(userId);
    }
}
