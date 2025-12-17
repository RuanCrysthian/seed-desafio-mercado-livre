package com.rfdev.desafio_mercado_livre.configuracao.validacao;

import java.time.LocalDateTime;
import java.util.List;

public record RespostaErro(
        LocalDateTime timestamp,
        int status,
        String erro,
        List<String> mensagens) {
}
