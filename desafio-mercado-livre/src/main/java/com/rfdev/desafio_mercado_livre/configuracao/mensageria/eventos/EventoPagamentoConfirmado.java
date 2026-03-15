package com.rfdev.desafio_mercado_livre.configuracao.mensageria.eventos;

import java.util.UUID;

public record EventoPagamentoConfirmado(UUID compraId) {
}
