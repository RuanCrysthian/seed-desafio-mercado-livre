package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import com.rfdev.desafio_mercado_livre.compra.TipoGatewayPagamento;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento.TradutorStatusPagsecuro;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento.TradutorStatusPaypal;

public class TradutorStatusTransacaoFactory {

    public static TradutorStatusTransacao criarTradutor(TipoGatewayPagamento tipoGateway) {
        return switch (tipoGateway) {
            case PAYPAL -> new TradutorStatusPaypal();
            case PAGSEGURO -> new TradutorStatusPagsecuro();
            default -> throw new IllegalArgumentException("Tipo de gateway de pagamento desconhecido: " + tipoGateway);
        };

    }
}
