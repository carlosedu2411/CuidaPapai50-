package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.*;
import com.cuidapapai.api.model.User;
import com.cuidapapai.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail é obrigatório.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha é obrigatória.");
        }
        if (userRepository.findByEmail(request.getEmail().trim()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
        }

        String shortName = request.getShortName();
        if (shortName == null || shortName.isBlank()) {
            shortName = request.getName() != null ? request.getName().split(" ")[0] : "Usuário";
        }

        String userId = "usr_" + UUID.randomUUID().toString().substring(0, 8);

        User newUser = User.builder()
                .id(userId)
                .name(request.getName() != null ? request.getName() : "Novo Usuário")
                .shortName(shortName)
                .email(request.getEmail().trim())
                .password(request.getPassword())
                .role(request.getRole() != null && !request.getRole().isBlank() ? request.getRole() : "elderly")
                .theme("light")
                .fontSize("large")
                .build();

        userRepository.save(newUser);

        return AuthResponse.builder()
                .success(true)
                .message("Usuário cadastrado com sucesso!")
                .token("token_" + newUser.getId())
                .user(toProfileResponse(newUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail é obrigatório.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha é obrigatória.");
        }

        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }

        return AuthResponse.builder()
                .success(true)
                .message("Login realizado com sucesso!")
                .token("token_" + user.getId())
                .user(toProfileResponse(user))
                .build();
    }

    private UserProfileResponse toProfileResponse(User user) {
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
