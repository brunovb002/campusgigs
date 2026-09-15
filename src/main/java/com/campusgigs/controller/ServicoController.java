package com.campusgigs.controller;

import com.campusgigs.dto.ContratacaoResponse;
import com.campusgigs.dto.ServicoRequest;
import com.campusgigs.dto.ServicoResponse;
import com.campusgigs.model.Usuario;
import com.campusgigs.repository.UsuarioRepository;
import com.campusgigs.service.ContratacaoService;
import com.campusgigs.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;
    private final ContratacaoService contratacaoService;
    private final UsuarioRepository usuarioRepository;

    public ServicoController(ServicoService servicoService,
                              ContratacaoService contratacaoService,
                              UsuarioRepository usuarioRepository) {
        this.servicoService = servicoService;
        this.contratacaoService = contratacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    // Qualquer usuário autenticado pode publicar.
    @PostMapping
    public ResponseEntity<ServicoResponse> publicar(@Valid @RequestBody ServicoRequest request,
                                                     Authentication authentication) {
        Usuario prestador = usuarioAutenticado(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.publicar(request, prestador));
    }

    // Listagem é pública: só operações que alteram dado exigem token.
    @GetMapping
    public ResponseEntity<List<ServicoResponse>> listar() {
        return ResponseEntity.ok(servicoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscar(id));
    }

    // Só o dono edita (checado no ServicoService); sem exceção para ADMIN aqui.
    @PatchMapping("/{id}")
    public ResponseEntity<ServicoResponse> editar(@PathVariable Long id,
                                                   @Valid @RequestBody ServicoRequest request,
                                                   Authentication authentication) {
        Usuario usuarioLogado = usuarioAutenticado(authentication);
        return ResponseEntity.ok(servicoService.editar(id, request, usuarioLogado));
    }

    // Dono OU ADMIN podem encerrar (checado no ServicoService).
    @PostMapping("/{id}/encerrar")
    public ResponseEntity<ServicoResponse> encerrar(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = usuarioAutenticado(authentication);
        return ResponseEntity.ok(servicoService.encerrar(id, usuarioLogado));
    }

    // Qualquer autenticado contrata, exceto o próprio dono do serviço.
    @PostMapping("/{id}/contratar")
    public ResponseEntity<ContratacaoResponse> contratar(@PathVariable Long id, Authentication authentication) {
        Usuario contratante = usuarioAutenticado(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(contratacaoService.contratar(id, contratante));
    }

    private Usuario usuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
    }
}
