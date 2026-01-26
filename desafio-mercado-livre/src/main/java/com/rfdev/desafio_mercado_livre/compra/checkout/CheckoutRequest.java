package com.rfdev.desafio_mercado_livre.compra.checkout;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.compra.CompraFactory;
import com.rfdev.desafio_mercado_livre.configuracao.validacao.EntidadeExiste;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigInteger;
import java.util.UUID;

public record CheckoutRequest(
        @NotNull @EntidadeExiste(message = "Produto não encontrado.", nomeTabela = Produto.class, nomeCampo = "id") UUID produtoId,
        @NotNull @Positive BigInteger quantidade,
        @NotBlank String tipoGatewayPagamento
) {

    public Compra toModel(
            Produto produto,
            Usuario comprador
    ) {
        return CompraFactory.criar(produto, this.quantidade, comprador, this.tipoGatewayPagamento);
    }
}
