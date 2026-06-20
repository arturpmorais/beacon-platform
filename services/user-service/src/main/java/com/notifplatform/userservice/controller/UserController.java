package com.notifplatform.userservice.controller;

import com.notifplatform.userservice.domain.enums.NotificationChannel;
import com.notifplatform.userservice.dto.request.CreateUserRequest;
import com.notifplatform.userservice.dto.request.UpdatePreferenceRequest;
import com.notifplatform.userservice.dto.response.PreferenceResponse;
import com.notifplatform.userservice.dto.response.UserResponse;
import com.notifplatform.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{externalId}")
    public UserResponse getByExternalId(@PathVariable String externalId) {
        return userService.getByExternalId(externalId);
    }

    // used by workers before delivering a notification
    @GetMapping("/{externalId}/preferences/{channel}")
    public PreferenceResponse getPreference(
            @PathVariable String externalId,
            @PathVariable NotificationChannel channel) {
        return userService.getPreference(externalId, channel);
    }

    @PutMapping("/{externalId}/preferences")
    public PreferenceResponse updatePreference(
            @PathVariable String externalId,
            @Valid @RequestBody UpdatePreferenceRequest request) {
        return userService.updatePreference(externalId, request);
    }
}
