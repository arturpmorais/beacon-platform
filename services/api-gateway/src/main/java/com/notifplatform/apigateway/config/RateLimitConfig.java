package com.notifplatform.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    // rate limit key: JWT subject when authenticated, IP address as fallback
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            var principal = exchange.getPrincipal();
            return principal
                    .map(p -> p.getName())
                    .switchIfEmpty(Mono.justOrEmpty(
                            exchange.getRequest().getRemoteAddress() != null
                                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                                    : "unknown"
                    ));
        };
    }
}
