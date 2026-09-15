package com.campusgigs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Cobre email inexistente ou senha errada em POST /auth/login (BadCredentialsException
    // e demais subtipos de AuthenticationException) — sempre a mesma mensagem genérica,
    // para não revelar se o problema foi o email ou a senha.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAutenticacao(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(corpoErro(HttpStatus.UNAUTHORIZED, "credenciais inválidas"));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailJaCadastrado(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpoErro(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ServicoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleServicoNaoEncontrado(ServicoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpoErro(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // Autorização decidida na regra de negócio (ex.: "só o dono edita") — 403,
    // mesmo formato do RestAccessDeniedHandler usado nas negações do Spring Security.
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, Object>> handleAcessoNegado(AcessoNegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(corpoErro(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    @ExceptionHandler(ContratacaoInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleContratacaoInvalida(ContratacaoInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpoErro(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.put(erro.getField(), erro.getDefaultMessage()));

        Map<String, Object> corpo = corpoErro(HttpStatus.BAD_REQUEST, "dados inválidos");
        corpo.put("campos", campos);
        return ResponseEntity.badRequest().body(corpo);
    }

    private Map<String, Object> corpoErro(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        return corpo;
    }
}
