package com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento;

import com.rfdev.desafio_mercado_livre.compra.PagamentoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TradutorStatusPaypalTest {

    private TradutorStatusPaypal tradutor;

    @BeforeEach
    void setUp() {
        tradutor = new TradutorStatusPaypal();
    }

    @Test
    void deveTraduzirStatus1ParaPagamentoStatusSUCESSO() {
        // When - testando com String "1"
        PagamentoStatus resultado = tradutor.traduzirStatus("1");

        // Then
        assertEquals(PagamentoStatus.SUCESSO, resultado);
    }

    @Test
    void deveTraduzirStatus1InteiroParaPagamentoStatusSUCESSO() {
        // When - testando com Integer 1
        PagamentoStatus resultado = tradutor.traduzirStatus(1);

        // Then
        assertEquals(PagamentoStatus.SUCESSO, resultado);
    }

    @Test
    void deveTraduzirStatus0ParaPagamentoStatusFALHA() {
        // When - testando com String "0"
        PagamentoStatus resultado = tradutor.traduzirStatus("0");

        // Then
        assertEquals(PagamentoStatus.FALHA, resultado);
    }

    @Test
    void deveTraduzirStatus0InteiroParaPagamentoStatusFALHA() {
        // When - testando com Integer 0
        PagamentoStatus resultado = tradutor.traduzirStatus(0);

        // Then
        assertEquals(PagamentoStatus.FALHA, resultado);
    }

    @Test
    void deveLancarExcecaoParaStatusDesconhecido() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tradutor.traduzirStatus("DESCONHECIDO");
        });

        assertTrue(exception.getMessage().contains("Status de transação desconhecido"));
    }

    @Test
    void deveLancarExcecaoParaStatusInteiroDesconhecido() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tradutor.traduzirStatus(999);
        });

        assertTrue(exception.getMessage().contains("Status de transação desconhecido"));
    }
}

