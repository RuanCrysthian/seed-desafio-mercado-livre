package com.rfdev.desafio_mercado_livre.configuracao.mensageria;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "mercadolivre.exchange";

    public static final String QUEUE_EMAIL_PAGAMENTO_CONFIRMADO = "q.email.pagamento.confirmado";
    public static final String QUEUE_EMAIL_PAGAMENTO_FALHOU = "q.email.pagamento.falhou";
    public static final String QUEUE_EMAIL_COMPRA_CRIADA = "q.email.compra.criada";
    public static final String QUEUE_EMAIL_PERGUNTA_CRIADA = "q.email.pergunta.criada";
    public static final String QUEUE_NOTAFISCAL = "q.notafiscal.processar";
    public static final String QUEUE_RANKING = "q.ranking.atualizar";

    public static final String RK_PAGAMENTO_CONFIRMADO = "pagamento.confirmado";
    public static final String RK_PAGAMENTO_FALHOU = "pagamento.falhou";
    public static final String RK_COMPRA_CRIADA = "compra.criada";
    public static final String RK_PERGUNTA_CRIADA = "pergunta.criada";

    @Bean
    TopicExchange mercadolivreExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean Queue queueEmailPagamentoConfirmado() { return new Queue(QUEUE_EMAIL_PAGAMENTO_CONFIRMADO); }
    @Bean Queue queueEmailPagamentoFalhou() { return new Queue(QUEUE_EMAIL_PAGAMENTO_FALHOU); }
    @Bean Queue queueEmailCompraCriada() { return new Queue(QUEUE_EMAIL_COMPRA_CRIADA); }
    @Bean Queue queueEmailPerguntaCriada() { return new Queue(QUEUE_EMAIL_PERGUNTA_CRIADA); }
    @Bean Queue queueNotaFiscal() { return new Queue(QUEUE_NOTAFISCAL); }
    @Bean Queue queueRanking() { return new Queue(QUEUE_RANKING); }

    @Bean Binding bindingEmailPagamentoConfirmado() {
        return BindingBuilder.bind(queueEmailPagamentoConfirmado()).to(mercadolivreExchange()).with(RK_PAGAMENTO_CONFIRMADO);
    }
    @Bean Binding bindingEmailPagamentoFalhou() {
        return BindingBuilder.bind(queueEmailPagamentoFalhou()).to(mercadolivreExchange()).with(RK_PAGAMENTO_FALHOU);
    }
    @Bean Binding bindingEmailCompraCriada() {
        return BindingBuilder.bind(queueEmailCompraCriada()).to(mercadolivreExchange()).with(RK_COMPRA_CRIADA);
    }
    @Bean Binding bindingEmailPerguntaCriada() {
        return BindingBuilder.bind(queueEmailPerguntaCriada()).to(mercadolivreExchange()).with(RK_PERGUNTA_CRIADA);
    }
    @Bean Binding bindingNotaFiscal() {
        return BindingBuilder.bind(queueNotaFiscal()).to(mercadolivreExchange()).with(RK_PAGAMENTO_CONFIRMADO);
    }
    @Bean Binding bindingRanking() {
        return BindingBuilder.bind(queueRanking()).to(mercadolivreExchange()).with(RK_PAGAMENTO_CONFIRMADO);
    }

    @Bean
    JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
