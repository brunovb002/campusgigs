package com.campusgigs.security;

import com.campusgigs.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chaveAssinatura;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                       @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.chaveAssinatura = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("papel", usuario.getPapel().name())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chaveAssinatura)
                .compact();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    // Retorna o email (subject) do token. Lança JwtException (assinatura inválida,
    // token expirado, malformado etc.) se o token não for confiável — quem chama decide
    // como reagir; o filtro trata isso como "requisição não autenticada".
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chaveAssinatura)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
