package com.rfdev.desafio_mercado_livre.compra.processar;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.configuracao.validacao.EntidadeExiste;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GatewayPagamentoWebhookRequest(
        @NotNull @EntidadeExiste(message = "Compra não encontrada", nomeTabela = Compra.class, nomeCampo = "id") UUID compraOrigemId,
        @NotNull UUID transacaoGatewayId,
        @NotNull Object statusTransacao
) {

}
