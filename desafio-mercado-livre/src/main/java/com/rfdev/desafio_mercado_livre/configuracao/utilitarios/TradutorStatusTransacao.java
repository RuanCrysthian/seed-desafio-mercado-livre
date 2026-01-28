package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import com.rfdev.desafio_mercado_livre.compra.PagamentoStatus;

public interface TradutorStatusTransacao {
    PagamentoStatus traduzirStatus(Object statusTransacao);
}
