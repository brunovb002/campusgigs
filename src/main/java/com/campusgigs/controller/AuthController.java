package com.campusgigs.controller;

import com.campusgigs.dto.CadastroRequest;
import com.campusgigs.dto.LoginRequest;
import com.campusgigs.dto.TokenResponse;
import com.campusgigs.dto.UsuarioResponse;
import com.campusgigs.model.Usuario;
import com.campusgigs.repository.UsuarioRepository;
import com.campusgigs.security.JwtService;
import com.campusgigs.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService,
                           UsuarioRepository usuarioRepository,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Endpoint público: qualquer um se cadastra, sempre como USER.
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        UsuarioResponse response = usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint público: valida email+senha e devolve um token JWT.
    // Credenciais inválidas geram AuthenticationException, tratada pelo
    // GlobalExceptionHandler como 401.
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.gerarToken(usuario);

        return ResponseEntity.ok(TokenResponse.bearer(token, jwtService.getExpirationMs()));
    }

    // Endpoint protegido: exige "Authorization: Bearer <token>" válido.
    // Serve para comprovar, na prática, que emissão/validação do JWT está funcionando.
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }
}
