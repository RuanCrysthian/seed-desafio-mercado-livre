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
    public static final String DLX = "mercadolivre.dlx";

    public static final String QUEUE_EMAIL_PAGAMENTO_CONFIRMADO = "q.email.pagamento.confirmado";
    public static final String QUEUE_EMAIL_PAGAMENTO_FALHOU = "q.email.pagamento.falhou";
    public static final String QUEUE_EMAIL_COMPRA_CRIADA = "q.email.compra.criada";
    public static final String QUEUE_EMAIL_PERGUNTA_CRIADA = "q.email.pergunta.criada";
    public static final String QUEUE_NOTAFISCAL = "q.notafiscal.processar";
    public static final String QUEUE_RANKING = "q.ranking.atualizar";

    public static final String QUEUE_EMAIL_PAGAMENTO_CONFIRMADO_DLQ = QUEUE_EMAIL_PAGAMENTO_CONFIRMADO + ".dlq";
    public static final String QUEUE_EMAIL_PAGAMENTO_FALHOU_DLQ = QUEUE_EMAIL_PAGAMENTO_FALHOU + ".dlq";
    public static final String QUEUE_EMAIL_COMPRA_CRIADA_DLQ = QUEUE_EMAIL_COMPRA_CRIADA + ".dlq";
    public static final String QUEUE_EMAIL_PERGUNTA_CRIADA_DLQ = QUEUE_EMAIL_PERGUNTA_CRIADA + ".dlq";
    public static final String QUEUE_NOTAFISCAL_DLQ = QUEUE_NOTAFISCAL + ".dlq";
    public static final String QUEUE_RANKING_DLQ = QUEUE_RANKING + ".dlq";

    public static final String RK_PAGAMENTO_CONFIRMADO = "pagamento.confirmado";
    public static final String RK_PAGAMENTO_FALHOU = "pagamento.falhou";
    public static final String RK_COMPRA_CRIADA = "compra.criada";
    public static final String RK_PERGUNTA_CRIADA = "pergunta.criada";

    @Bean
    TopicExchange mercadolivreExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX);
    }

    @Bean Queue queueEmailPagamentoConfirmado() { return withDlx(QUEUE_EMAIL_PAGAMENTO_CONFIRMADO); }
    @Bean Queue queueEmailPagamentoFalhou() { return withDlx(QUEUE_EMAIL_PAGAMENTO_FALHOU); }
    @Bean Queue queueEmailCompraCriada() { return withDlx(QUEUE_EMAIL_COMPRA_CRIADA); }
    @Bean Queue queueEmailPerguntaCriada() { return withDlx(QUEUE_EMAIL_PERGUNTA_CRIADA); }
    @Bean Queue queueNotaFiscal() { return withDlx(QUEUE_NOTAFISCAL); }
    @Bean Queue queueRanking() { return withDlx(QUEUE_RANKING); }

    @Bean Queue queueEmailPagamentoConfirmadoDlq() { return QueueBuilder.durable(QUEUE_EMAIL_PAGAMENTO_CONFIRMADO_DLQ).build(); }
    @Bean Queue queueEmailPagamentoFalhouDlq() { return QueueBuilder.durable(QUEUE_EMAIL_PAGAMENTO_FALHOU_DLQ).build(); }
    @Bean Queue queueEmailCompraCriadaDlq() { return QueueBuilder.durable(QUEUE_EMAIL_COMPRA_CRIADA_DLQ).build(); }
    @Bean Queue queueEmailPerguntaCriadaDlq() { return QueueBuilder.durable(QUEUE_EMAIL_PERGUNTA_CRIADA_DLQ).build(); }
    @Bean Queue queueNotaFiscalDlq() { return QueueBuilder.durable(QUEUE_NOTAFISCAL_DLQ).build(); }
    @Bean Queue queueRankingDlq() { return QueueBuilder.durable(QUEUE_RANKING_DLQ).build(); }

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

    @Bean Binding bindingEmailPagamentoConfirmadoDlq() {
        return BindingBuilder.bind(queueEmailPagamentoConfirmadoDlq()).to(deadLetterExchange()).with(QUEUE_EMAIL_PAGAMENTO_CONFIRMADO);
    }
    @Bean Binding bindingEmailPagamentoFalhouDlq() {
        return BindingBuilder.bind(queueEmailPagamentoFalhouDlq()).to(deadLetterExchange()).with(QUEUE_EMAIL_PAGAMENTO_FALHOU);
    }
    @Bean Binding bindingEmailCompraCriadaDlq() {
        return BindingBuilder.bind(queueEmailCompraCriadaDlq()).to(deadLetterExchange()).with(QUEUE_EMAIL_COMPRA_CRIADA);
    }
    @Bean Binding bindingEmailPerguntaCriadaDlq() {
        return BindingBuilder.bind(queueEmailPerguntaCriadaDlq()).to(deadLetterExchange()).with(QUEUE_EMAIL_PERGUNTA_CRIADA);
    }
    @Bean Binding bindingNotaFiscalDlq() {
        return BindingBuilder.bind(queueNotaFiscalDlq()).to(deadLetterExchange()).with(QUEUE_NOTAFISCAL);
    }
    @Bean Binding bindingRankingDlq() {
        return BindingBuilder.bind(queueRankingDlq()).to(deadLetterExchange()).with(QUEUE_RANKING);
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

    private Queue withDlx(String queueName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", queueName)
                .build();
    }
}
