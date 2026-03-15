package com.rfdev.desafio_mercado_livre.configuracao.mensageria.consumers;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.compra.CompraRepository;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.RabbitMQConfig;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.eventos.EventoCompraCriada;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.EnviadorEmail;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailCompraConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailCompraConsumer.class);

    private final CompraRepository compraRepository;
    private final EnviadorEmail enviadorEmail;

    public EmailCompraConsumer(CompraRepository compraRepository, EnviadorEmail enviadorEmail) {
        this.compraRepository = compraRepository;
        this.enviadorEmail = enviadorEmail;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_COMPRA_CRIADA)
    public void onCompraCriada(EventoCompraCriada evento) {
        logger.info("Processando e-mail de intenção de compra para compra {}", evento.compraId());
        Compra compra = compraRepository.findById(evento.compraId())
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada: " + evento.compraId()));
        enviadorEmail.enviarEmailDesejoCompra(compra);
    }
}
