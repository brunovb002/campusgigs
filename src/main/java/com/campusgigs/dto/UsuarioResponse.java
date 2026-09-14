package com.campusgigs.dto;

import com.campusgigs.model.Papel;
import com.campusgigs.model.Usuario;

// DTO de saída: nunca inclui a senha (nem o hash).
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cep,
        String cidade,
        String uf,
        Papel papel
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCep(),
                usuario.getCidade(),
                usuario.getUf(),
                usuario.getPapel()
        );
    }
}
