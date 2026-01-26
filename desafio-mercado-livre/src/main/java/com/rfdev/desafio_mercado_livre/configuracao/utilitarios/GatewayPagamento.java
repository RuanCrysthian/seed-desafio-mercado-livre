package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import java.util.UUID;

public interface GatewayPagamento {
    String processarCompra(UUID compraId);
}
