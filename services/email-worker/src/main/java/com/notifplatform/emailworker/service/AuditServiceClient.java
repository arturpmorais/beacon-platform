package com.notifplatform.emailworker.service;

import com.notifplatform.emailworker.dto.AuditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class AuditServiceClient {

    private final WebClient webClient;

    public AuditServiceClient(@Qualifier("auditServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public void log(AuditRequest request) {
        // audit failure must never affect delivery
        webClient.post()
                .uri("/audit")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(ex -> log.warn("failed to log audit for {}: {}", request.getNotificationId(), ex.getMessage()))
                .onErrorComplete()
                .subscribe();
    }
}
