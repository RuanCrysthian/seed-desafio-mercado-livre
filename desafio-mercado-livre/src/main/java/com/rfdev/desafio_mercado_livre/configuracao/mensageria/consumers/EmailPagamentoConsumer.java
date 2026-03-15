package com.rfdev.desafio_mercado_livre.configuracao.mensageria.consumers;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.compra.CompraRepository;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.RabbitMQConfig;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.eventos.EventoPagamentoConfirmado;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.eventos.EventoPagamentoFalhou;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.EnviadorEmail;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailPagamentoConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailPagamentoConsumer.class);

    private final CompraRepository compraRepository;
    private final EnviadorEmail enviadorEmail;

    public EmailPagamentoConsumer(CompraRepository compraRepository, EnviadorEmail enviadorEmail) {
        this.compraRepository = compraRepository;
        this.enviadorEmail = enviadorEmail;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_PAGAMENTO_CONFIRMADO)
    public void onPagamentoConfirmado(EventoPagamentoConfirmado evento) {
        logger.info("Processando e-mail de pagamento confirmado para compra {}", evento.compraId());
        Compra compra = compraRepository.findById(evento.compraId())
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada: " + evento.compraId()));
        enviadorEmail.enviarEmailCompraConfirmada(compra);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_PAGAMENTO_FALHOU)
    public void onPagamentoFalhou(EventoPagamentoFalhou evento) {
        logger.info("Processando e-mail de pagamento falhou para compra {}", evento.compraId());
        Compra compra = compraRepository.findById(evento.compraId())
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada: " + evento.compraId()));
        enviadorEmail.enviarEmailPagamentoFalhou(compra);
    }
}
