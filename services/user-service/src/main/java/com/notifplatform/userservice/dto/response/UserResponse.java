package com.notifplatform.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String externalId;
    private String email;
    private String phone;
    private String pushToken;
    private List<PreferenceResponse> preferences;
}
