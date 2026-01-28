package com.rfdev.desafio_mercado_livre.compra;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transacoes_pagamento")
@Getter
public class TransacaoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transacao_pagamento_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    @NotNull
    private Compra compraOrigem;

    @Column(name = "transacao_gateway_id", nullable = false, unique = true)
    @NotNull
    private UUID transacaoGatewayId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private PagamentoStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_gateway_pagamento", nullable = false)
    @NotNull
    private TipoGatewayPagamento tipoGatewayPagamento;

    @Column(name = "data_criacao", nullable = false)
    @NotNull
    private Instant dataCriacao;

    @Deprecated
    public TransacaoPagamento() {
    }

    public TransacaoPagamento(
            @NotNull Compra compraOrigem,
            @NotNull UUID transacaoGatewayId,
            @NotNull PagamentoStatus status,
            @NotNull TipoGatewayPagamento tipoGatewayPagamento) {
        this.compraOrigem = compraOrigem;
        this.transacaoGatewayId = transacaoGatewayId;
        this.status = status;
        this.tipoGatewayPagamento = tipoGatewayPagamento;
        this.dataCriacao = Instant.now();
    }

}
