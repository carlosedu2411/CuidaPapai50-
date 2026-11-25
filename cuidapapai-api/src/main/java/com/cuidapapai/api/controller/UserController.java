package com.cuidapapai.api.controller;

import com.cuidapapai.api.config.UserResolver;
import com.cuidapapai.api.dto.UserProfileResponse;
import com.cuidapapai.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserResolver userResolver;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            HttpServletRequest request,
            @RequestParam(name = "user_id", required = false) String userId
    ) {
        String resolvedUserId = userResolver.resolveUserId(request, userId);
        return ResponseEntity.ok(userService.getCurrentUser(resolvedUserId));
    }
}
