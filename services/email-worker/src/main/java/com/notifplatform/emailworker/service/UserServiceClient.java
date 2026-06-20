package com.notifplatform.emailworker.service;

import com.notifplatform.emailworker.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(@Qualifier("userServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<UserResponse> getUser(String externalId) {
        try {
            UserResponse user = webClient.get()
                    .uri("/users/{externalId}", externalId)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();
            return Optional.ofNullable(user);
        } catch (Exception ex) {
            log.error("failed to fetch user {}: {}", externalId, ex.getMessage());
            return Optional.empty();
        }
    }
}
