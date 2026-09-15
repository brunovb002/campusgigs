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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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

    // Atualiza o CEP (e cidade/uf derivados) de um usuário já cadastrado.
    // Recebe o id, não a entidade, para garantir que o Usuario carregado aqui
    // está gerenciado por esta transação (permite salvar por dirty checking).
    @Transactional
    public UsuarioResponse atualizarCep(Long usuarioId, AtualizarCepRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();

        EnderecoCep endereco = cepService.consultar(request.cep());

        usuario.setCep(request.cep());
        usuario.setCidade(endereco.cidade());
        usuario.setUf(endereco.uf());

        return UsuarioResponse.from(usuario);
    }
}
