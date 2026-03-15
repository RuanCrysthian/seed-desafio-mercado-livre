package com.rfdev.desafio_mercado_livre.configuracao.mensageria.consumers;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.compra.CompraRepository;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.RabbitMQConfig;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.eventos.EventoPagamentoConfirmado;
import com.rfdev.desafio_mercado_livre.vendedores.RankingVendedoresService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RankingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RankingConsumer.class);

    private final CompraRepository compraRepository;
    private final RankingVendedoresService rankingVendedoresService;

    public RankingConsumer(CompraRepository compraRepository, RankingVendedoresService rankingVendedoresService) {
        this.compraRepository = compraRepository;
        this.rankingVendedoresService = rankingVendedoresService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RANKING)
    public void onPagamentoConfirmado(EventoPagamentoConfirmado evento) {
        logger.info("Atualizando ranking de vendedores para compra {}", evento.compraId());
        Compra compra = compraRepository.findById(evento.compraId())
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada: " + evento.compraId()));
        rankingVendedoresService.notificarVenda(compra);
    }
}
