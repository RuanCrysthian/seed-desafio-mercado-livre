package com.rfdev.desafio_mercado_livre.compra;

import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigInteger;
import java.util.UUID;

@Entity
@Table(name = "compras")
@Getter
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "compra_id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Positive
    @Column(name = "quantidade", nullable = false)
    private BigInteger quantidade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_gateway_pagamento", nullable = false)
    private TipoGatewayPagamento tipoGatewayPagamento;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    @NotNull
    private Usuario comprador;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CompraStatus status;

    @Deprecated
    public Compra() {
    }

    private Compra(
            @NotNull Produto produto,
            @Positive @NotNull BigInteger quantidade,
            @NotNull TipoGatewayPagamento tipoGatewayPagamento, Usuario comprador) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.tipoGatewayPagamento = tipoGatewayPagamento;
        this.comprador = comprador;
        this.status = CompraStatus.INICIADA;
    }

    public static Compra criarPagamentoPaypal(
            @NotNull Produto produto,
            @Positive @NotNull BigInteger quantidade,
            @NotNull Usuario comprador) {
        return new Compra(produto, quantidade, TipoGatewayPagamento.PAYPAL, comprador);
    }

    public static Compra criarPagamentoPagseguro(
            @NotNull Produto produto,
            @Positive @NotNull BigInteger quantidade,
            @NotNull Usuario comprador) {
        return new Compra(produto, quantidade, TipoGatewayPagamento.PAGSEGURO, comprador);
    }

    public UUID getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public BigInteger getQuantidade() {
        return quantidade;
    }

    public TipoGatewayPagamento getTipoGatewayPagamento() {
        return tipoGatewayPagamento;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public CompraStatus getStatus() {
        return status;
    }

    public boolean compraFoiConcluidaComSucesso() {
        return this.status == CompraStatus.CONCLUIDA;
    }

    public void concluirCompra() {
        this.status = CompraStatus.CONCLUIDA;
    }
}
