package com.rfdev.desafio_mercado_livre.configuracao.mensageria.consumers;

import com.rfdev.desafio_mercado_livre.configuracao.mensageria.RabbitMQConfig;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.eventos.EventoPerguntaCriada;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.EnviadorEmail;
import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.pergunta.PerguntaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailPerguntaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailPerguntaConsumer.class);

    private final PerguntaRepository perguntaRepository;
    private final EnviadorEmail enviadorEmail;

    public EmailPerguntaConsumer(PerguntaRepository perguntaRepository, EnviadorEmail enviadorEmail) {
        this.perguntaRepository = perguntaRepository;
        this.enviadorEmail = enviadorEmail;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_PERGUNTA_CRIADA)
    public void onPerguntaCriada(EventoPerguntaCriada evento) {
        logger.info("Processando e-mail de nova pergunta {}", evento.perguntaId());
        Pergunta pergunta = perguntaRepository.findById(evento.perguntaId())
                .orElseThrow(() -> new EntityNotFoundException("Pergunta não encontrada: " + evento.perguntaId()));
        enviadorEmail.enviarEmailNovaPergunta(pergunta);
    }
}
