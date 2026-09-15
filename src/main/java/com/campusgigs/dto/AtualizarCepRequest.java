package com.campusgigs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizarCepRequest(

        @NotBlank(message = "cep é obrigatório")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "cep deve estar no formato 00000-000")
        String cep
) {
}
