package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import com.rfdev.desafio_mercado_livre.compra.TipoGatewayPagamento;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento.PagseguroGateway;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento.PayPalGateway;

public class GatewayPagamentoFactory {

    public static GatewayPagamento criar(TipoGatewayPagamento tipoGatewayPagamento) {
        return switch (tipoGatewayPagamento) {
            case PAYPAL -> new PayPalGateway();
            case PAGSEGURO -> new PagseguroGateway();
        };
    }
}
