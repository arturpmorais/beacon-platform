package com.notifplatform.emailworker.consumer;

import com.notifplatform.emailworker.dto.AuditRequest;
import com.notifplatform.emailworker.dto.NotificationEvent;
import com.notifplatform.emailworker.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final UserServiceClient userServiceClient;
    private final AuditServiceClient auditServiceClient;
    private final EmailSender emailSender;
    private final EmailTemplateService templateService;

    @KafkaListener(
            topics = "${kafka.topics.email}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, NotificationEvent> record, Acknowledgment ack) {
        NotificationEvent event = record.value();
        log.info("received event {} for user {} type {}", event.getNotificationId(), event.getUserId(), event.getType());

        try {
            // fetch user contact info
            var userOpt = userServiceClient.getUser(event.getUserId());
            if (userOpt.isEmpty() || userOpt.get().getEmail() == null) {
                log.warn("no email address for user {}, skipping", event.getUserId());
                audit(event, "SKIPPED", "no email address", null);
                ack.acknowledge();
                return;
            }

            String email = userOpt.get().getEmail();
            String subject = templateService.subject(event);
            String body = templateService.body(event);

            String providerResponse = emailSender.send(email, subject, body);

            audit(event, "SENT", null, providerResponse);
            log.info("email sent for notification {} to {}", event.getNotificationId(), email);

        } catch (Exception ex) {
            // re-throw so the error handler retries and eventually routes to DLQ
            log.error("failed to process notification {}: {}", event.getNotificationId(), ex.getMessage());
            audit(event, "FAILED", ex.getMessage(), null);
            throw ex;
        }

        // only ack after successful processing — prevents message loss on crash
        ack.acknowledge();
    }

    private void audit(NotificationEvent event, String status, String error, String providerResponse) {
        auditServiceClient.log(AuditRequest.builder()
                .notificationId(event.getNotificationId())
                .userId(event.getUserId())
                .channel("EMAIL")
                .status(status)
                .errorMessage(error)
                .providerResponse(providerResponse)
                .build());
    }
}
