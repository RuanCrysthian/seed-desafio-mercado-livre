package com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento;

import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.GatewayPagamento;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PayPalGateway implements GatewayPagamento {

    //    paypal.com/{idGeradoDaCompra}?redirectUrl={urlRetornoAppPosPagamento}
    private final String PATH = "paypal.com/";
    private final String REDIRECT_URL = "?redirectUrl=";
    private final String URL_RETORNO_APP_POS_PAGAMENTO = "http://localhost:8080/retorno-pagamento-paypal";

    @Override
    public String processarCompra(UUID compraId) {
        return PATH + compraId.toString() + REDIRECT_URL + URL_RETORNO_APP_POS_PAGAMENTO;
    }
}
