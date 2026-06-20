package com.notifplatform.emailworker.service;

import com.notifplatform.emailworker.dto.NotificationEvent;
import org.springframework.stereotype.Service;

import java.util.Map;

// #TODO: extract to a template engine if it gets more complex
@Service
public class EmailTemplateService {

    public String subject(NotificationEvent event) {
        return switch (event.getType()) {
            case "ORDER_SHIPPED"    -> "Your order is on its way!";
            case "ORDER_DELIVERED" -> "Your order has been delivered";
            case "PASSWORD_RESET"  -> "Reset your password";
            case "WELCOME"         -> "Welcome to Beacon Platform";
            default                -> "You have a new notification";
        };
    }

    public String body(NotificationEvent event) {
        Map<String, Object> data = event.getData();
        return switch (event.getType()) {
            case "ORDER_SHIPPED" -> String.format(
                    "Hi! Your order is on its way. Tracking code: %s.",
                    data.getOrDefault("trackingCode", "N/A")
            );
            case "ORDER_DELIVERED" -> "Your order has been delivered. Enjoy!";
            case "PASSWORD_RESET"  -> String.format(
                    "Click the link to reset your password: %s",
                    data.getOrDefault("resetLink", "#")
            );
            case "WELCOME"         -> "Welcome! Your account is ready.";
            default                -> String.format("Notification type: %s", event.getType());
        };
    }
}
