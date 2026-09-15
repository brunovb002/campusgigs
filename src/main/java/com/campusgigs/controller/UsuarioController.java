package com.campusgigs.controller;

import com.campusgigs.dto.AtualizarCepRequest;
import com.campusgigs.dto.UsuarioResponse;
import com.campusgigs.model.Usuario;
import com.campusgigs.repository.UsuarioRepository;
import com.campusgigs.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // Atualiza o CEP só do próprio usuário autenticado (nunca de outro) e
    // reconsulta cidade/uf no serviço externo.
    @PatchMapping("/me/cep")
    public ResponseEntity<UsuarioResponse> atualizarCep(@Valid @RequestBody AtualizarCepRequest request,
                                                          Authentication authentication) {
        Usuario usuarioLogado = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(usuarioService.atualizarCep(usuarioLogado.getId(), request));
    }
}
