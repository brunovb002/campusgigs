package com.campusgigs.dto;

import com.campusgigs.model.Servico;
import com.campusgigs.model.SituacaoServico;

import java.math.BigDecimal;

public record ServicoResponse(
        Long id,
        Long prestadorId,
        String prestadorNome,
        String titulo,
        String descricao,
        String categoria,
        BigDecimal preco,
        SituacaoServico situacao
) {
    public static ServicoResponse from(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getPrestador().getId(),
                servico.getPrestador().getNome(),
                servico.getTitulo(),
                servico.getDescricao(),
                servico.getCategoria(),
                servico.getPreco(),
                servico.getSituacao()
        );
    }
}
