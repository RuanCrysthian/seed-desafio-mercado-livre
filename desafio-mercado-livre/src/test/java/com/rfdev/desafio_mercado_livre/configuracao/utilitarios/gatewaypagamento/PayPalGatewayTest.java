package com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayPalGatewayTest {

    @Test
    void deveRetornarUrlCorretaParaProcessarCompra() {
        PayPalGateway payPalGateway = new PayPalGateway();
        String compraId = "123e4567-e89b-12d3-a456-426614174000";
        String urlEsperada = "paypal.com/123e4567-e89b-12d3-a456-426614174000?redirectUrl=http://localhost:8080/retorno-pagamento-paypal";

        String urlGerada = payPalGateway.processarCompra(java.util.UUID.fromString(compraId));

        assertEquals(urlEsperada, urlGerada);
    }

}