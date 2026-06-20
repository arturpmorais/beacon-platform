package com.notifplatform.userservice.service;

import com.notifplatform.userservice.domain.entity.NotificationPreference;
import com.notifplatform.userservice.domain.entity.User;
import com.notifplatform.userservice.domain.enums.NotificationChannel;
import com.notifplatform.userservice.dto.request.CreateUserRequest;
import com.notifplatform.userservice.dto.request.UpdatePreferenceRequest;
import com.notifplatform.userservice.dto.response.PreferenceResponse;
import com.notifplatform.userservice.dto.response.UserResponse;
import com.notifplatform.userservice.exception.UserNotFoundException;
import com.notifplatform.userservice.repository.NotificationPreferenceRepository;
import com.notifplatform.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByExternalId(request.getExternalId())) {
            throw new IllegalArgumentException("user already exists: " + request.getExternalId());
        }

        User user = User.builder()
                .externalId(request.getExternalId())
                .email(request.getEmail())
                .phone(request.getPhone())
                .pushToken(request.getPushToken())
                .build();

        userRepository.save(user);

        // create default preferences: all channels enabled, no quiet hours
        Arrays.stream(NotificationChannel.values()).forEach(channel -> {
            NotificationPreference pref = NotificationPreference.builder()
                    .user(user)
                    .channel(channel)
                    .enabled(true)
                    .build();
            preferenceRepository.save(pref);
        });

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getByExternalId(String externalId) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException(externalId));
        return toResponse(user);
    }

    @Transactional
    public PreferenceResponse updatePreference(String externalId, UpdatePreferenceRequest request) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException(externalId));

        NotificationPreference pref = preferenceRepository
                .findByUserIdAndChannel(user.getId(), request.getChannel())
                .orElseGet(() -> NotificationPreference.builder()
                        .user(user)
                        .channel(request.getChannel())
                        .build());

        pref.setEnabled(request.getEnabled());
        pref.setQuietStart(request.getQuietStart());
        pref.setQuietEnd(request.getQuietEnd());
        preferenceRepository.save(pref);

        return toPreferenceResponse(pref);
    }

    // used by workers to check if delivery is allowed
    @Transactional(readOnly = true)
    public PreferenceResponse getPreference(String externalId, NotificationChannel channel) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException(externalId));

        return preferenceRepository.findByUserIdAndChannel(user.getId(), channel)
                .map(this::toPreferenceResponse)
                .orElse(PreferenceResponse.builder().channel(channel).enabled(true).build());
    }

    private UserResponse toResponse(User user) {
        List<PreferenceResponse> prefs = preferenceRepository.findAllByUserId(user.getId())
                .stream().map(this::toPreferenceResponse).toList();

        return UserResponse.builder()
                .id(user.getId())
                .externalId(user.getExternalId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .pushToken(user.getPushToken())
                .preferences(prefs)
                .build();
    }

    private PreferenceResponse toPreferenceResponse(NotificationPreference pref) {
        return PreferenceResponse.builder()
                .channel(pref.getChannel())
                .enabled(pref.isEnabled())
                .quietStart(pref.getQuietStart())
                .quietEnd(pref.getQuietEnd())
                .build();
    }
}
