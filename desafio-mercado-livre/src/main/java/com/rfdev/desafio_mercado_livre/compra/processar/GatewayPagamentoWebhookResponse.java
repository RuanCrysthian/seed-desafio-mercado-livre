package com.rfdev.desafio_mercado_livre.compra.processar;

import com.rfdev.desafio_mercado_livre.compra.TransacaoPagamento;

import java.time.Instant;

public record GatewayPagamentoWebhookResponse(
        String status,
        Instant dataPagamento
) {

    public static GatewayPagamentoWebhookResponse of(TransacaoPagamento transacaoPagamento) {
        return new GatewayPagamentoWebhookResponse(
                transacaoPagamento.getStatus().name(), transacaoPagamento.getDataCriacao());
    }
}
