package com.rfdev.desafio_mercado_livre.usuario.cadastro;

import org.hibernate.validator.constraints.Length;

import com.rfdev.desafio_mercado_livre.configuracao.seguranca.PasswordEncoder;
import com.rfdev.desafio_mercado_livre.configuracao.validacao.CampoUnico;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastroUsuarioRequest(
        @NotBlank @Email(message = "Email inválido.") @CampoUnico(message = "Email já cadastrado.", nomeTabela = Usuario.class, nomeCampo = "login") String login,
        @NotBlank @Length(min = 6, message = "Senha fraca.") String senha) {

    public Usuario toModel(PasswordEncoder passwordEncoder) {
        return new Usuario(this.login, passwordEncoder.encode(this.senha));
    }
}
