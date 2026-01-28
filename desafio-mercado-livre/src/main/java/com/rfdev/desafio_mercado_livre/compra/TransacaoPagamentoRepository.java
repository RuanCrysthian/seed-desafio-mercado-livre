package com.rfdev.desafio_mercado_livre.compra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransacaoPagamentoRepository extends JpaRepository<TransacaoPagamento, UUID> {
    boolean existsByTransacaoGatewayId(UUID transacaoGatewayId);

    boolean existsByCompraOrigemAndTipoGatewayPagamento(Compra compraOrigem, TipoGatewayPagamento tipoGatewayPagamento);
}
