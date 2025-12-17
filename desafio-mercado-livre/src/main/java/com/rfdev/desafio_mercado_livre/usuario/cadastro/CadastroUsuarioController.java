package com.rfdev.desafio_mercado_livre.usuario.cadastro;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rfdev.desafio_mercado_livre.configuracao.seguranca.PasswordEncoder;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import com.rfdev.desafio_mercado_livre.usuario.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
public class CadastroUsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroUsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/usuarios")
    @Transactional
    public ResponseEntity<CadastroUsuarioResponse> cadastrar(@RequestBody @Valid CadastroUsuarioRequest request) {
        Usuario usuario = request.toModel(passwordEncoder);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(CadastroUsuarioResponse.of(usuario));
    }
}
