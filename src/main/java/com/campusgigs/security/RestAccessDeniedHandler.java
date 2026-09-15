package com.campusgigs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

// Chamado quando o usuário está autenticado (token válido), mas o papel dele não
// permite a operação. Já deixado pronto aqui; passa a ser exercitado de fato a
// partir do CP4, quando as regras de autorização por papel entrarem em vigor.
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", HttpStatus.FORBIDDEN.value());
        corpo.put("erro", HttpStatus.FORBIDDEN.getReasonPhrase());
        corpo.put("mensagem", "seu papel não tem permissão para esta operação");

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(corpo));
    }
}
