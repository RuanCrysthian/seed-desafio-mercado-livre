package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import com.rfdev.desafio_mercado_livre.compra.TipoGatewayPagamento;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento.TradutorStatusPagseguro;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento.TradutorStatusPaypal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TradutorStatusTransacaoFactoryTest {

    @Test
    void deveCriarTradutorPaypal() {
        // When
        TradutorStatusTransacao tradutor = TradutorStatusTransacaoFactory.criarTradutor(TipoGatewayPagamento.PAYPAL);

        // Then
        assertNotNull(tradutor);
        assertInstanceOf(TradutorStatusPaypal.class, tradutor);
    }

    @Test
    void deveCriarTradutorPagseguro() {
        // When
        TradutorStatusTransacao tradutor = TradutorStatusTransacaoFactory.criarTradutor(TipoGatewayPagamento.PAGSEGURO);

        // Then
        assertNotNull(tradutor);
        assertInstanceOf(TradutorStatusPagseguro.class, tradutor);
    }

    @Test
    void deveLancarExcecaoParaGatewayDesconhecido() {
        // When & Then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            TradutorStatusTransacaoFactory.criarTradutor(null);
        });

        assertNotNull(exception);
    }
}


