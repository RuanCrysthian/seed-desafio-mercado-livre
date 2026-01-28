package com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TradutorStatusPagseguroTest {

    @Test
    void deveTraduzirStatusSUCESSOparaPagamentoStatusSUCESSO() {
        TradutorStatusPagseguro tradutor = new TradutorStatusPagseguro();
        assert tradutor.traduzirStatus("SUCESSO") == com.rfdev.desafio_mercado_livre.compra.PagamentoStatus.SUCESSO;
    }

    @Test
    void deveTraduzirStatusERROparaPagamentoStatusFALHA() {

        TradutorStatusPagseguro tradutor = new TradutorStatusPagseguro();
        assert tradutor.traduzirStatus("ERRO") == com.rfdev.desafio_mercado_livre.compra.PagamentoStatus.FALHA;
    }

    @Test
    void deveLancarExcecaoParaStatusDesconhecido() {
        TradutorStatusPagseguro tradutor = new TradutorStatusPagseguro();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            tradutor.traduzirStatus("DESCONHECIDO");
        });
    }
}