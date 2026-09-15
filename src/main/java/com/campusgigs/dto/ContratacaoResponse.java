package com.campusgigs.dto;

import com.campusgigs.model.Contratacao;
import com.campusgigs.model.SituacaoContratacao;

import java.time.LocalDateTime;

public record ContratacaoResponse(
        Long id,
        Long servicoId,
        String servicoTitulo,
        Long contratanteId,
        String contratanteNome,
        SituacaoContratacao situacao,
        LocalDateTime dataContratacao
) {
    public static ContratacaoResponse from(Contratacao contratacao) {
        return new ContratacaoResponse(
                contratacao.getId(),
                contratacao.getServico().getId(),
                contratacao.getServico().getTitulo(),
                contratacao.getContratante().getId(),
                contratacao.getContratante().getNome(),
                contratacao.getSituacao(),
                contratacao.getDataContratacao()
        );
    }
}
