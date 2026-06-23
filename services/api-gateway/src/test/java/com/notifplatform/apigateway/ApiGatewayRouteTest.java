package com.notifplatform.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiGatewayRouteTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void shouldRejectRequestWithoutToken() {
        webClient.post()
                .uri("/api/v1/notifications")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldAllowActuatorHealthWithoutToken() {
        webClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }
}
