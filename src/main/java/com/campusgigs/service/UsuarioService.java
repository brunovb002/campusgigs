package com.campusgigs.service;

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

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        // Cadastro público sempre cria papel USER — promover a ADMIN é operação
        // administrativa, nunca decidida pelo próprio cliente da API.
        Usuario usuario = new Usuario(
                request.nome(),
                request.email(),
                passwordEncoder.encode(request.senha()),
                request.cep(),
                Papel.USER
        );

        // cidade/uf ficam null aqui; serão preenchidos pela integração de CEP (CP5).
        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.from(usuario);
    }
}
