package com.rfdev.desafio_mercado_livre.notafiscal.cadastro;

import java.util.UUID;

public record CadastroNotaFiscalResponse(
        UUID notaFiscalId,
        UUID compraId,
        UUID compradorId,
        String status,
        String mensagem
) {
    public static CadastroNotaFiscalResponse sucesso(UUID compraId, UUID compradorId) {
        return new CadastroNotaFiscalResponse(
                UUID.randomUUID(),
                compraId,
                compradorId,
                "PROCESSADA",
                "Nota fiscal gerada com sucesso"
        );
    }
}
