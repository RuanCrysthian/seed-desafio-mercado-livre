package com.rfdev.desafio_mercado_livre.configuracao.seguranca;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import com.rfdev.desafio_mercado_livre.usuario.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
public class LoginController {

    private final AutenticacaoService autenticacaoService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;

    public LoginController(AutenticacaoService autenticacaoService,
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository) {
        this.autenticacaoService = autenticacaoService;
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken autenticationToken = new UsernamePasswordAuthenticationToken(
                request.login(), request.senha());
        Authentication authentication = authenticationManager.authenticate(autenticationToken);

        Usuario usuario = usuarioRepository.findByLogin(authentication.getName()).orElseThrow(
                () -> new IllegalArgumentException("Login ou senha incorretos"));

        return ResponseEntity.ok(autenticacaoService.gerarToken(usuario));
    }
}
