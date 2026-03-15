package com.rfdev.desafio_mercado_livre.configuracao.mensageria.consumers;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.compra.CompraRepository;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.RabbitMQConfig;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.eventos.EventoPagamentoConfirmado;
import com.rfdev.desafio_mercado_livre.notafiscal.NotaFiscalService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotaFiscalConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotaFiscalConsumer.class);

    private final CompraRepository compraRepository;
    private final NotaFiscalService notaFiscalService;

    public NotaFiscalConsumer(CompraRepository compraRepository, NotaFiscalService notaFiscalService) {
        this.compraRepository = compraRepository;
        this.notaFiscalService = notaFiscalService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTAFISCAL)
    public void onPagamentoConfirmado(EventoPagamentoConfirmado evento) {
        logger.info("Processando nota fiscal para compra {}", evento.compraId());
        Compra compra = compraRepository.findById(evento.compraId())
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada: " + evento.compraId()));
        notaFiscalService.notificarCompraConcluida(compra);
    }
}
