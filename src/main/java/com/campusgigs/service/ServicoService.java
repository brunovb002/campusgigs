package com.campusgigs.service;

import com.campusgigs.dto.ServicoRequest;
import com.campusgigs.dto.ServicoResponse;
import com.campusgigs.exception.AcessoNegadoException;
import com.campusgigs.exception.ServicoNaoEncontradoException;
import com.campusgigs.model.Papel;
import com.campusgigs.model.Servico;
import com.campusgigs.model.SituacaoServico;
import com.campusgigs.model.Usuario;
import com.campusgigs.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional
    public ServicoResponse publicar(ServicoRequest request, Usuario prestador) {
        Servico servico = new Servico(prestador, request.titulo(), request.descricao(),
                request.categoria(), request.preco());
        servico = servicoRepository.save(servico);
        return ServicoResponse.from(servico);
    }

    public List<ServicoResponse> listar() {
        return servicoRepository.findAll().stream()
                .map(ServicoResponse::from)
                .toList();
    }

    public ServicoResponse buscar(Long id) {
        return ServicoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ServicoResponse editar(Long id, ServicoRequest request, Usuario usuarioLogado) {
        Servico servico = buscarEntidade(id);

        // Editar é sempre restrito ao dono do serviço — diferente de "encerrar",
        // aqui não existe exceção para ADMIN (ver justificativa do commit do CP4).
        if (!ehDono(servico, usuarioLogado)) {
            throw new AcessoNegadoException("apenas o prestador dono do serviço pode editá-lo");
        }

        servico.setTitulo(request.titulo());
        servico.setDescricao(request.descricao());
        servico.setCategoria(request.categoria());
        servico.setPreco(request.preco());

        return ServicoResponse.from(servico);
    }

    @Transactional
    public ServicoResponse encerrar(Long id, Usuario usuarioLogado) {
        Servico servico = buscarEntidade(id);

        boolean autorizado = ehDono(servico, usuarioLogado) || usuarioLogado.getPapel() == Papel.ADMIN;
        if (!autorizado) {
            throw new AcessoNegadoException("apenas o prestador dono do serviço ou um ADMIN podem encerrá-lo");
        }

        servico.setSituacao(SituacaoServico.ENCERRADO);
        return ServicoResponse.from(servico);
    }

    // Pacote-visível: usado também pelo ContratacaoService para localizar o serviço
    // a ser contratado, sem duplicar a checagem de "não encontrado".
    Servico buscarEntidade(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException(id));
    }

    private boolean ehDono(Servico servico, Usuario usuario) {
        return servico.getPrestador().getId().equals(usuario.getId());
    }
}
