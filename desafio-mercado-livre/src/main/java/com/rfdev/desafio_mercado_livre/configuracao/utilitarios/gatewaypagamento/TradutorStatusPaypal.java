package com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento;

import com.rfdev.desafio_mercado_livre.compra.PagamentoStatus;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.TradutorStatusTransacao;
import org.springframework.stereotype.Component;

@Component
public class TradutorStatusPaypal implements TradutorStatusTransacao {

    @Override
    public PagamentoStatus traduzirStatus(Object statusTransacao) {
        if (statusTransacao.equals("1")) {
            return PagamentoStatus.SUCESSO;
        } else if (statusTransacao.equals("0")) {
            return PagamentoStatus.FALHA;
        }
        throw new IllegalArgumentException("Status de transação desconhecido: " + statusTransacao);
    }
}
