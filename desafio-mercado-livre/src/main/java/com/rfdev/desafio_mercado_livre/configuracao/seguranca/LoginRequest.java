package com.rfdev.desafio_mercado_livre.configuracao.seguranca;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email Obrigatório") @Email(message = "Email Inválido") String login,
        @NotBlank(message = "Senha Obrigatória") String senha) {

}
