package com.rfdev.desafio_mercado_livre.notafiscal.cadastro;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CadastroNotaFiscalRequest(
        @NotNull UUID compraId,
        @NotNull UUID compradorId
) {
}
