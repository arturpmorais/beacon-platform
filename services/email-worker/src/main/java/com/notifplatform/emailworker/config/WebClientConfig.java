package com.notifplatform.emailworker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient(@Value("${services.user-service.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }

    @Bean
    public WebClient auditServiceWebClient(@Value("${services.audit-service.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }
}
