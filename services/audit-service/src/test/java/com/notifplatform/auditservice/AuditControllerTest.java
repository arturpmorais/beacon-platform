package com.notifplatform.auditservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifplatform.auditservice.dto.AuditRequest;
import com.notifplatform.auditservice.repository.NotificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired NotificationLogRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveAndReturnAuditLog() throws Exception {
        UUID notificationId = UUID.randomUUID();

        AuditRequest request = new AuditRequest();
        request.setNotificationId(notificationId);
        request.setUserId("user-001");
        request.setChannel("EMAIL");
        request.setStatus("SENT");
        request.setProviderResponse("mock-msg-id-123");

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.channel").value("EMAIL"));
    }

    @Test
    void shouldReturnLogsByNotificationId() throws Exception {
        UUID notificationId = UUID.randomUUID();

        AuditRequest request = new AuditRequest();
        request.setNotificationId(notificationId);
        request.setUserId("user-001");
        request.setChannel("SMS");
        request.setStatus("FAILED");
        request.setErrorMessage("provider timeout");

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/audit/notifications/{id}", notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("FAILED"));
    }

    @Test
    void shouldReturnAllLogsForUser() throws Exception {
        UUID notif1 = UUID.randomUUID();
        UUID notif2 = UUID.randomUUID();

        for (UUID id : new UUID[]{notif1, notif2}) {
            AuditRequest req = new AuditRequest();
            req.setNotificationId(id);
            req.setUserId("user-001");
            req.setChannel("EMAIL");
            req.setStatus("SENT");

            mockMvc.perform(post("/audit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/audit/users/user-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
