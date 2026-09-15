package com.campusgigs.service;

import com.campusgigs.dto.ContratacaoResponse;
import com.campusgigs.exception.ContratacaoInvalidaException;
import com.campusgigs.model.Contratacao;
import com.campusgigs.model.Servico;
import com.campusgigs.model.SituacaoServico;
import com.campusgigs.model.Usuario;
import com.campusgigs.repository.ContratacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;
    private final ServicoService servicoService;

    public ContratacaoService(ContratacaoRepository contratacaoRepository, ServicoService servicoService) {
        this.contratacaoRepository = contratacaoRepository;
        this.servicoService = servicoService;
    }

    @Transactional
    public ContratacaoResponse contratar(Long servicoId, Usuario contratante) {
        Servico servico = servicoService.buscarEntidade(servicoId);

        if (servico.getPrestador().getId().equals(contratante.getId())) {
            throw new ContratacaoInvalidaException("você não pode contratar o próprio serviço");
        }
        if (servico.getSituacao() != SituacaoServico.ATIVO) {
            throw new ContratacaoInvalidaException("este serviço não está ativo no momento");
        }

        Contratacao contratacao = new Contratacao(servico, contratante);
        contratacao = contratacaoRepository.save(contratacao);
        return ContratacaoResponse.from(contratacao);
    }
}
