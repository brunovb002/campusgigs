package com.campusgigs.service;

import com.campusgigs.dto.AtualizarCepRequest;
import com.campusgigs.dto.CadastroRequest;
import com.campusgigs.dto.UsuarioResponse;
import com.campusgigs.exception.EmailJaCadastradoException;
import com.campusgigs.model.Papel;
import com.campusgigs.model.Usuario;
import com.campusgigs.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CepService cepService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, CepService cepService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.cepService = cepService;
    }

    // Sem @Transactional no método inteiro de propósito: a consulta ao ViaCEP é uma
    // chamada de rede que pode demorar até o timeout configurado, e não queremos
    // segurar uma conexão do pool do banco (Hikari) presa esperando um serviço externo
    // responder. existsByEmail/save já são transacionais por conta própria (Spring Data).
    public UsuarioResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        // Consulta o CEP ANTES de gravar: se ele não existir ou o serviço externo
        // falhar, o cadastro inteiro é recusado — nunca fica um usuário salvo com
        // cidade/uf incompletos (ver CepService/CepIndisponivelException).
        EnderecoCep endereco = cepService.consultar(request.cep());

        // Cadastro público sempre cria papel USER — promover a ADMIN é operação
        // administrativa, nunca decidida pelo próprio cliente da API.
        Usuario usuario = new Usuario(
                request.nome(),
                request.email(),
                passwordEncoder.encode(request.senha()),
                request.cep(),
                Papel.USER
        );
        usuario.setCidade(endereco.cidade());
        usuario.setUf(endereco.uf());

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.from(usuario);
    }

    // Atualiza o CEP (e cidade/uf derivados) de um usuário já cadastrado. Recebe a
    // entidade já carregada pelo controller (evita uma segunda consulta ao banco pelo
    // mesmo id) e salva explicitamente no final — sem @Transactional, pelo mesmo
    // motivo do cadastrar(): a chamada ao ViaCEP não deve segurar conexão do pool.
    public UsuarioResponse atualizarCep(Usuario usuario, AtualizarCepRequest request) {
        EnderecoCep endereco = cepService.consultar(request.cep());

        usuario.setCep(request.cep());
        usuario.setCidade(endereco.cidade());
        usuario.setUf(endereco.uf());

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.from(usuario);
    }
}
