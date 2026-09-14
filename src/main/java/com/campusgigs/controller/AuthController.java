package com.campusgigs.controller;

import com.campusgigs.dto.CadastroRequest;
import com.campusgigs.dto.UsuarioResponse;
import com.campusgigs.model.Usuario;
import com.campusgigs.repository.UsuarioRepository;
import com.campusgigs.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // Endpoint público: qualquer um se cadastra, sempre como USER.
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        UsuarioResponse response = usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint protegido: exige autenticação (HTTP Basic no CP2; JWT a partir do CP3).
    // Serve para comprovar, na prática, que login/autenticação está funcionando.
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }
}
