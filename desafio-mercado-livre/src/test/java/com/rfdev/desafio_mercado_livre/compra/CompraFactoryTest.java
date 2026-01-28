package com.rfdev.desafio_mercado_livre.compra;

import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompraFactoryTest {

    private Produto produto;
    private Usuario comprador;
    private BigInteger quantidade;

    @BeforeEach
    void setUp() {
        // Cria uma categoria para o produto
        Categoria categoria = new Categoria("Eletrônicos", null);

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");

        // Cria um produto
        produto = new Produto(
                "Smartphone XYZ",
                new BigDecimal("1500.00"),
                BigInteger.valueOf(10),
                List.of("128GB", "5G", "Câmera 48MP"),
                "Smartphone com ótima câmera",
                categoria,
                vendedor);

        // Cria um usuário comprador
        comprador = new Usuario("comprador@email.com", "SenhaForte123!");

        // Define a quantidade da compra
        quantidade = BigInteger.valueOf(2);
    }

    @Test
    void deveCriarCompraComGatewayPaypal() {
        // When
        Compra compra = CompraFactory.criar(produto, quantidade, comprador, "PAYPAL");

        // Then
        assertNotNull(compra);
        assertEquals(produto, compra.getProduto());
        assertEquals(quantidade, compra.getQuantidade());
        assertEquals(comprador, compra.getComprador());
        assertEquals(TipoGatewayPagamento.PAYPAL, compra.getTipoGatewayPagamento());
        assertEquals(CompraStatus.INICIADA, compra.getStatus());
    }

    @Test
    void deveCriarCompraComGatewayPagseguro() {
        // When
        Compra compra = CompraFactory.criar(produto, quantidade, comprador, "PAGSEGURO");

        // Then
        assertNotNull(compra);
        assertEquals(produto, compra.getProduto());
        assertEquals(quantidade, compra.getQuantidade());
        assertEquals(comprador, compra.getComprador());
        assertEquals(TipoGatewayPagamento.PAGSEGURO, compra.getTipoGatewayPagamento());
        assertEquals(CompraStatus.INICIADA, compra.getStatus());
    }

    @Test
    void deveLancarExcecaoParaGatewayInvalido() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CompraFactory.criar(produto, quantidade, comprador, "GATEWAY_INVALIDO");
        });

        assertEquals("Tipo de gateway de pagamento inválido.", exception.getMessage());
    }

}

