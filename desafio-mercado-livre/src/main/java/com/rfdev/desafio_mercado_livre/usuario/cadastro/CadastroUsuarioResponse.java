package com.rfdev.desafio_mercado_livre.usuario.cadastro;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.rfdev.desafio_mercado_livre.usuario.Usuario;

public record CadastroUsuarioResponse(
        UUID id,
        String login,
        ZonedDateTime criadoEm) {

    public static CadastroUsuarioResponse of(Usuario usuario) {
        return new CadastroUsuarioResponse(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getCriadoEm());
    }

}
