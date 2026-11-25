package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.UserPreferencesDto;
import com.cuidapapai.api.dto.UserProfileResponse;
import com.cuidapapai.api.model.User;
import com.cuidapapai.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getCurrentUser(String userId) {
        User user;
        if (userId != null && !userId.isBlank()) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com id: " + userId));
        } else {
            user = userRepository.findFirstByOrderByIdAsc()
                    .orElseGet(() -> User.builder()
                            .id("usr_987654")
                            .name("Maria da Silva")
                            .shortName("Dona Maria")
                            .role("elderly")
                            .theme("light")
                            .fontSize("large")
                            .build());
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .shortName(user.getShortName())
                .role(user.getRole())
                .preferences(UserPreferencesDto.builder()
                        .theme(user.getTheme())
                        .fontSize(user.getFontSize())
                        .build())
                .build();
    }
}
