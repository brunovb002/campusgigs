package com.campusgigs.dto;

// tipo "Bearer": indica como o cliente deve enviar o token de volta,
// no header "Authorization: Bearer <token>".
public record TokenResponse(String token, String tipo, long expiraEmMs) {

    public static TokenResponse bearer(String token, long expiraEmMs) {
        return new TokenResponse(token, "Bearer", expiraEmMs);
    }
}
