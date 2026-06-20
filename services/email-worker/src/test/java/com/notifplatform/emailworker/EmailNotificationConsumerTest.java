package com.notifplatform.emailworker;

import com.notifplatform.emailworker.consumer.EmailNotificationConsumer;
import com.notifplatform.emailworker.dto.NotificationEvent;
import com.notifplatform.emailworker.dto.UserResponse;
import com.notifplatform.emailworker.service.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationConsumerTest {

    @Mock UserServiceClient userServiceClient;
    @Mock AuditServiceClient auditServiceClient;
    @Mock EmailSender emailSender;
    @Mock EmailTemplateService templateService;
    @Mock Acknowledgment ack;

    @InjectMocks EmailNotificationConsumer consumer;

    @Test
    void shouldSendEmailAndAckWhenUserHasEmail() {
        var event = buildEvent("ORDER_SHIPPED");
        var user = new UserResponse();
        user.setEmail("artur@test.com");

        when(userServiceClient.getUser("user-001")).thenReturn(Optional.of(user));
        when(templateService.subject(event)).thenReturn("Your order is on its way!");
        when(templateService.body(event)).thenReturn("Tracking: BR123");
        when(emailSender.send(anyString(), anyString(), anyString())).thenReturn("msg-id-123");

        consumer.consume(record(event), ack);

        verify(emailSender).send("artur@test.com", "Your order is on its way!", "Tracking: BR123");
        verify(auditServiceClient).log(argThat(r -> "SENT".equals(r.getStatus())));
        verify(ack).acknowledge();
    }

    @Test
    void shouldSkipAndAckWhenUserHasNoEmail() {
        var event = buildEvent("ORDER_SHIPPED");
        var user = new UserResponse();
        user.setEmail(null);

        when(userServiceClient.getUser("user-001")).thenReturn(Optional.of(user));

        consumer.consume(record(event), ack);

        verify(emailSender, never()).send(any(), any(), any());
        verify(auditServiceClient).log(argThat(r -> "SKIPPED".equals(r.getStatus())));
        verify(ack).acknowledge();
    }

    @Test
    void shouldAuditFailedAndRethrowOnException() {
        var event = buildEvent("ORDER_SHIPPED");
        var user = new UserResponse();
        user.setEmail("artur@test.com");

        when(userServiceClient.getUser("user-001")).thenReturn(Optional.of(user));
        when(templateService.subject(any())).thenReturn("subject");
        when(templateService.body(any())).thenReturn("body");
        when(emailSender.send(any(), any(), any())).thenThrow(new RuntimeException("provider timeout"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> consumer.consume(record(event), ack));

        verify(auditServiceClient).log(argThat(r -> "FAILED".equals(r.getStatus())));
        // must NOT ack on failure
        verify(ack, never()).acknowledge();
    }

    private NotificationEvent buildEvent(String type) {
        NotificationEvent e = new NotificationEvent();
        e.setNotificationId(UUID.randomUUID());
        e.setUserId("user-001");
        e.setType(type);
        e.setData(Map.of("trackingCode", "BR123"));
        e.setCreatedAt(Instant.now());
        return e;
    }

    private ConsumerRecord<String, NotificationEvent> record(NotificationEvent event) {
        return new ConsumerRecord<>("notifications.email", 0, 0L, event.getUserId(), event);
    }
}
