package com.rfdev.desafio_mercado_livre.vendedores;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
            logger.info("📊 Atualizando o ranking dos vendedores para o vendedorId: {}", vendedor);
//            AdicionarVendaRankingRequest request = new AdicionarVendaRankingRequest(
//                    compra.getId(),
//                    vendedor.getId()
//            );
//            String url = "http://localhost:8080/api/ranking-vendedores";
//            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            logger.error("❌ Erro ao comunicar com o sistema de ranking de vendedores", e);
        }

    }
}
