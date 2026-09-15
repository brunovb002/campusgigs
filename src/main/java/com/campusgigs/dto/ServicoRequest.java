package com.campusgigs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServicoRequest(

        @NotBlank(message = "título é obrigatório")
        String titulo,

        @NotBlank(message = "descrição é obrigatória")
        @Size(max = 1000, message = "descrição deve ter no máximo 1000 caracteres")
        String descricao,

        @NotBlank(message = "categoria é obrigatória")
        String categoria,

        @NotNull(message = "preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = true, message = "preço não pode ser negativo")
        BigDecimal preco
) {
}
