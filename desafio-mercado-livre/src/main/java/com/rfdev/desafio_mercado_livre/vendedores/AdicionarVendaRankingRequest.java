package com.rfdev.desafio_mercado_livre.vendedores;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AdicionarVendaRankingRequest(
        @NotNull UUID compraId,
        @NotNull UUID vendedorId
) {
}
