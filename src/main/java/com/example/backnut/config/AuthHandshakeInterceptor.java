package com.example.backnut.config;

import com.example.backnut.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtUtil jwtUtil;

    public AuthHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = null;

        // Extract token from query parameters or header.
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            token = servletRequest.getParameter("token");
            if (token == null) {
                token = servletRequest.getHeader("Authorization");
            }
        }

        System.out.println("Verifying token");
        if (token != null && validateToken(token)) {
            Long userId = jwtUtil.extractUserId(token);
            System.out.println("Valid token, userId: " + userId);
            // Store the userId in handshake attributes for later use.
            attributes.put("userId", userId);
            return true;
        }
        // Reject handshake if token is invalid.
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) { }

    // Validate the token and ensure a userId can be extracted.
    private boolean validateToken(String token) {
        Long userId = jwtUtil.extractUserId(token);
        return userId != null;
    }
}
