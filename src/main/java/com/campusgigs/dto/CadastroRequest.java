package com.campusgigs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Não expõe "papel": todo cadastro público nasce como USER (ver UsuarioService).
public record CadastroRequest(

        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,

        @NotBlank(message = "senha é obrigatória")
        @Size(min = 6, message = "senha deve ter ao menos 6 caracteres")
        String senha,

        @NotBlank(message = "cep é obrigatório")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "cep deve estar no formato 00000-000")
        String cep
) {
}
