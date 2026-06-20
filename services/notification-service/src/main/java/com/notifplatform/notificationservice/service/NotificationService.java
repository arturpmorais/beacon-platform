package com.notifplatform.notificationservice.service;

import com.notifplatform.notificationservice.domain.enums.NotificationChannel;
import com.notifplatform.notificationservice.dto.NotificationEvent;
import com.notifplatform.notificationservice.dto.SendNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    @Value("${kafka.topics.email}")
    private String emailTopic;

    @Value("${kafka.topics.sms}")
    private String smsTopic;

    @Value("${kafka.topics.push}")
    private String pushTopic;

    public UUID dispatch(SendNotificationRequest request) {
        UUID notificationId = UUID.randomUUID();

        NotificationEvent event = NotificationEvent.builder()
                .notificationId(notificationId)
                .userId(request.getUserId())
                .type(request.getType())
                .data(request.getData())
                .createdAt(Instant.now())
                .build();

        // publish to each channel if user has it enabled
        publishIfEnabled(event, NotificationChannel.EMAIL, emailTopic);
        publishIfEnabled(event, NotificationChannel.SMS, smsTopic);
        publishIfEnabled(event, NotificationChannel.PUSH, pushTopic);

        log.info("dispatched notification {} for user {} type {}", notificationId, request.getUserId(), request.getType());

        return notificationId;
    }

    private void publishIfEnabled(NotificationEvent event, NotificationChannel channel, String topic) {
        if (!userServiceClient.isChannelEnabled(event.getUserId(), channel)) {
            return;
        }

        // use userId as partition key: same user always goes to same partition, preserving order
        kafkaTemplate.send(topic, event.getUserId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("failed to publish to topic {} for user {}: {}", topic, event.getUserId(), ex.getMessage());
                    } else {
                        log.debug("published to {} partition {} offset {}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
