package com.rfdev.desafio_mercado_livre.vendedores;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class RankingVendedoresController {

    private static final Logger logger = LoggerFactory.getLogger(RankingVendedoresController.class);

    @PostMapping("/api/ranking-vendedores")
    public ResponseEntity<String> atualizarRankingVendedores(@RequestBody @Valid AdicionarVendaRankingRequest request) {
        logger.info("📊 Atualizando o ranking dos vendedores...");
        // Lógica para atualizar o ranking dos vendedores
        return ResponseEntity.ok("Ranking dos vendedores atualizado com sucesso!");
    }
}
