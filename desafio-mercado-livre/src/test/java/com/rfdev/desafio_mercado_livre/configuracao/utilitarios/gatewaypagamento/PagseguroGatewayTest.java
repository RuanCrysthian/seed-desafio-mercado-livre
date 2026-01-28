package com.rfdev.desafio_mercado_livre.configuracao.utilitarios.gatewaypagamento;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PagseguroGatewayTest {

    @Test
    void deveRetornarUrlCorretaParaProcessarCompra() {
        PagseguroGateway pagseguroGateway = new PagseguroGateway();
        String compraId = "123e4567-e89b-12d3-a456-426614174000";
        String urlEsperada = "pagseguro.com/?returnId=123e4567-e89b-12d3-a456-426614174000&?redirectUrl=http://localhost:8080/retorno-pagamento-pagseguro";

        String urlGerada = pagseguroGateway.processarCompra(java.util.UUID.fromString(compraId));

        assertEquals(urlEsperada, urlGerada);
    }

}