package com.rfdev.desafio_mercado_livre.usuario.cadastro;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rfdev.desafio_mercado_livre.configuracao.validacao.CampoUnico;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastroUsuarioRequest(
        @NotBlank @Email(message = "Email inválido.") @CampoUnico(message = "Email já cadastrado.", nomeTabela = Usuario.class, nomeCampo = "login") String login,
        @NotBlank @Length(min = 6, message = "Senha fraca.") String senha) {

    public Usuario toModel(PasswordEncoder passwordEncoder) {
        String senhaCriptografada = passwordEncoder.encode(this.senha);
        return new Usuario(this.login, senhaCriptografada);
    }
}
