package com.notifplatform.auditservice.service;

import com.notifplatform.auditservice.domain.entity.NotificationLog;
import com.notifplatform.auditservice.dto.AuditRequest;
import com.notifplatform.auditservice.dto.AuditResponse;
import com.notifplatform.auditservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final NotificationLogRepository repository;

    @Transactional
    public AuditResponse save(AuditRequest request) {
        NotificationLog log = NotificationLog.builder()
                .notificationId(request.getNotificationId())
                .userId(request.getUserId())
                .channel(request.getChannel())
                .status(request.getStatus())
                .errorMessage(request.getErrorMessage())
                .providerResponse(request.getProviderResponse())
                .build();

        return toResponse(repository.save(log));
    }

    @Transactional(readOnly = true)
    public List<AuditResponse> getByNotificationId(UUID notificationId) {
        return repository.findByNotificationIdOrderByCreatedAtDesc(notificationId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditResponse> getByUserId(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    private AuditResponse toResponse(NotificationLog log) {
        return AuditResponse.builder()
                .id(log.getId())
                .notificationId(log.getNotificationId())
                .userId(log.getUserId())
                .channel(log.getChannel())
                .status(log.getStatus())
                .errorMessage(log.getErrorMessage())
                .providerResponse(log.getProviderResponse())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
