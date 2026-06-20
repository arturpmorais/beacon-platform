package com.notifplatform.notificationservice;

import com.notifplatform.notificationservice.domain.enums.NotificationChannel;
import com.notifplatform.notificationservice.dto.NotificationEvent;
import com.notifplatform.notificationservice.dto.SendNotificationRequest;
import com.notifplatform.notificationservice.service.NotificationService;
import com.notifplatform.notificationservice.service.UserServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    @Mock UserServiceClient userServiceClient;
    @InjectMocks NotificationService notificationService;

    @Test
    void shouldPublishToAllTopicsWhenAllChannelsEnabled() {
        ReflectionTestUtils.setField(notificationService, "emailTopic", "notifications.email");
        ReflectionTestUtils.setField(notificationService, "smsTopic", "notifications.sms");
        ReflectionTestUtils.setField(notificationService, "pushTopic", "notifications.push");

        when(userServiceClient.isChannelEnabled(anyString(), any(NotificationChannel.class))).thenReturn(true);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        SendNotificationRequest request = new SendNotificationRequest();
        request.setUserId("user-001");
        request.setType("ORDER_SHIPPED");
        request.setData(Map.of("trackingCode", "BR123"));

        var id = notificationService.dispatch(request);

        assertThat(id).isNotNull();
        // one publish per channel
        verify(kafkaTemplate, times(3)).send(anyString(), eq("user-001"), any());
    }

    @Test
    void shouldSkipTopicWhenChannelDisabled() {
        ReflectionTestUtils.setField(notificationService, "emailTopic", "notifications.email");
        ReflectionTestUtils.setField(notificationService, "smsTopic", "notifications.sms");
        ReflectionTestUtils.setField(notificationService, "pushTopic", "notifications.push");

        // only EMAIL disabled
        when(userServiceClient.isChannelEnabled(anyString(), eq(NotificationChannel.EMAIL))).thenReturn(false);
        when(userServiceClient.isChannelEnabled(anyString(), eq(NotificationChannel.SMS))).thenReturn(true);
        when(userServiceClient.isChannelEnabled(anyString(), eq(NotificationChannel.PUSH))).thenReturn(true);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        SendNotificationRequest request = new SendNotificationRequest();
        request.setUserId("user-001");
        request.setType("ORDER_SHIPPED");
        request.setData(Map.of());

        notificationService.dispatch(request);

        // only SMS and PUSH published
        verify(kafkaTemplate, times(2)).send(anyString(), anyString(), any());
        verify(kafkaTemplate, never()).send(eq("notifications.email"), anyString(), any());
    }
}
