package com.rfdev.desafio_mercado_livre.notafiscal.cadastro;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller fake que simula um microsserviço de Nota Fiscal.
 * Este endpoint recebe informações de compras concluídas com sucesso
 * e simula o processamento de emissão de nota fiscal.
 */
@RestController
public class CadastroNotaFiscalController {

    private static final Logger logger = LoggerFactory.getLogger(CadastroNotaFiscalController.class);

    @PostMapping("/api/notas-fiscais")
    public ResponseEntity<CadastroNotaFiscalResponse> cadastrar(
            @RequestBody @Valid CadastroNotaFiscalRequest request) {

        logger.info("🧾 [SISTEMA DE NOTA FISCAL] Recebendo requisição para gerar nota fiscal");
        logger.info("   - ID da Compra: {}", request.compraId());
        logger.info("   - ID do Comprador: {}", request.compradorId());

        // Simula processamento da nota fiscal
        CadastroNotaFiscalResponse response = CadastroNotaFiscalResponse.sucesso(
                request.compraId(),
                request.compradorId()
        );

        logger.info("✅ [SISTEMA DE NOTA FISCAL] Nota fiscal gerada com sucesso!");
        logger.info("   - ID da Nota Fiscal: {}", response.notaFiscalId());

        return ResponseEntity.ok(response);
    }
}

