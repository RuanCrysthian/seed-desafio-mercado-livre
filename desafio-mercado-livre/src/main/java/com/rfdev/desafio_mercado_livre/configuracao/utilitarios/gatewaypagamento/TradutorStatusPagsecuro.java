package com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento;

import com.rfdev.desafio_mercado_livre.compra.PagamentoStatus;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.TradutorStatusTransacao;
import org.springframework.stereotype.Component;

@Component
public class TradutorStatusPagsecuro implements TradutorStatusTransacao {

    @Override
    public PagamentoStatus traduzirStatus(Object statusTransacao) {
        if (statusTransacao.equals("SUCESSO")) {
            return PagamentoStatus.SUCESSO;
        } else if (statusTransacao.equals("ERRO")) {
            return PagamentoStatus.FALHA;
        }
        throw new IllegalArgumentException("Status de transação desconhecido: " + statusTransacao);
    }
}
