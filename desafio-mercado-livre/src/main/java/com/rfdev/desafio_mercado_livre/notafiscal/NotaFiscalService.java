package com.rfdev.desafio_mercado_livre.notafiscal;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.notafiscal.cadastro.CadastroNotaFiscalRequest;
import com.rfdev.desafio_mercado_livre.notafiscal.cadastro.CadastroNotaFiscalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Serviço responsável por comunicar com o sistema de Nota Fiscal (microserviço fake).
 * Este serviço seria chamado após uma compra ser concluída com sucesso.
 */
@Service
public class NotaFiscalService {

    private static final Logger logger = LoggerFactory.getLogger(NotaFiscalService.class);
    private final RestTemplate restTemplate;

    public NotaFiscalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notificarCompraConcluida(Compra compra) {
        try {
            logger.info("📤 Enviando informações da compra {} para o sistema de nota fiscal", compra.getId());

            CadastroNotaFiscalRequest request = new CadastroNotaFiscalRequest(
                    compra.getId(),
                    compra.getComprador().getId()
            );

            // Em produção, essa URL seria configurável (application.properties)
            String url = "http://localhost:8080/api/notas-fiscais";

            CadastroNotaFiscalResponse response = restTemplate.postForObject(
                    url,
                    request,
                    CadastroNotaFiscalResponse.class
            );

            logger.info("✅ Nota fiscal processada com sucesso: {}", response);

        } catch (Exception e) {
            logger.error("❌ Erro ao comunicar com o sistema de nota fiscal", e);
            // Em produção, você pode querer implementar retry logic ou enfileirar para processamento posterior
        }
    }
}
