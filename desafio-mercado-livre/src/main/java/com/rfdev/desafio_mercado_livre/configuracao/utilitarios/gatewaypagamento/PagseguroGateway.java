package com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento;

import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.GatewayPagamento;

import java.util.UUID;

public class PagseguroGateway implements GatewayPagamento {

    // pagseguro.com?returnId={idGeradoDaCompra}&redirectUrl={urlRetornoAppPosPagamento}

    private final String PATH = "pagseguro.com/";
    private final String RETURN_ID = "?returnId=";
    private final String REDIRECT_URL = "&?redirectUrl=";
    private final String URL_RETORNO_APP_POS_PAGAMENTO = "http://localhost:8080/retorno-pagamento-pagseguro";

    @Override
    public String processarCompra(UUID compraId) {
        return PATH + RETURN_ID + compraId + REDIRECT_URL + URL_RETORNO_APP_POS_PAGAMENTO;
    }
}
