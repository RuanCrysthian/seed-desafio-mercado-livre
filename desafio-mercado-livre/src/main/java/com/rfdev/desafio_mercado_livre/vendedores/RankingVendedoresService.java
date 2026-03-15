package com.rfdev.desafio_mercado_livre.vendedores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

@Service
public class RankingVendedoresService {

    private static final Logger logger = LoggerFactory.getLogger(RankingVendedoresService.class);
    private final RestTemplate restTemplate;

    public RankingVendedoresService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notificarVenda(Compra compra) {
        try {
            Usuario vendedor = compra.getProduto().getUsuarioCriador();
            logger.info("📊 Atualizando o ranking dos vendedores para o vendedorId: {}", vendedor.getLogin());
        } catch (Exception e) {
            logger.error("❌ Erro ao comunicar com o sistema de ranking de vendedores", e);
        }

    }
}
