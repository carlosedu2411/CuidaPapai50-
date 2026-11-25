package com.cuidapapai.api.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UserResolver {

    public String resolveUserId(HttpServletRequest request) {
        String param = request != null ? request.getParameter("user_id") : null;
        return resolveUserId(request, param);
    }

    public String resolveUserId(HttpServletRequest request, String paramUserId) {
        if (paramUserId != null && !paramUserId.isBlank()) {
            return paramUserId.trim();
        }

        if (request == null) {
            return "usr_987654";
        }

        String xUserId = request.getHeader("X-User-Id");
        if (xUserId != null && !xUserId.isBlank()) {
            return xUserId.trim();
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (token.startsWith("token_")) {
                return token.substring(6).trim();
            }
            if (!token.isBlank()) {
                return token;
            }
        }

        // Padrão caso nenhuma autenticação seja enviada
        return "usr_987654";
    }
}
