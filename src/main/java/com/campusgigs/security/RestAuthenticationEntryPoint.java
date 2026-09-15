package com.campusgigs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

// Chamado pelo Spring Security quando um endpoint protegido é acessado sem
// autenticação válida (sem token, token expirado/malformado/assinatura inválida).
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", HttpStatus.UNAUTHORIZED.value());
        corpo.put("erro", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        corpo.put("mensagem", "token de acesso ausente ou inválido");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(corpo));
    }
}
