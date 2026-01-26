package com.rfdev.desafio_mercado_livre.compra;

import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import java.math.BigInteger;

public class CompraFactory {

    public static Compra criar(Produto produto, BigInteger quantidade, Usuario comprador, String tipoGatewayPagamento) {

        if (tipoGatewayPagamento.equalsIgnoreCase("PAYPAL")) {
            return Compra.criarPagamentoPaypal(produto, quantidade, comprador);
        } else if (tipoGatewayPagamento.equalsIgnoreCase("PAGSEGURO")) {
            return Compra.criarPagamentoPagseguro(produto, quantidade, comprador);
        } else {
            throw new IllegalArgumentException("Tipo de gateway de pagamento inválido.");
        }
    }
}
