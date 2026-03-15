package com.rfdev.desafio_mercado_livre;

import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

public class DatabaseTest {

    private static final PostgreSQLContainer<?> CONTAINER;
    private static final RabbitMQContainer RABBITMQ;

    static {
        CONTAINER = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);

        CONTAINER.start();

        // Configura propriedades GLOBAIS do banco
        System.setProperty("spring.datasource.url", CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", CONTAINER.getPassword());
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");
        System.setProperty("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect");
        System.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        RABBITMQ = new RabbitMQContainer("rabbitmq:3-management")
                .withReuse(true);

        RABBITMQ.start();

        // Configura propriedades GLOBAIS do RabbitMQ
        System.setProperty("spring.rabbitmq.host", RABBITMQ.getHost());
        System.setProperty("spring.rabbitmq.port", String.valueOf(RABBITMQ.getAmqpPort()));
        System.setProperty("spring.rabbitmq.username", RABBITMQ.getAdminUsername());
        System.setProperty("spring.rabbitmq.password", RABBITMQ.getAdminPassword());
    }

    @AfterAll
    static void tearDown() {
        if (CONTAINER != null) {
            CONTAINER.stop();
        }
        if (RABBITMQ != null) {
            RABBITMQ.stop();
        }
    }

    public static boolean isRunning() {
        return CONTAINER.isRunning();
    }
}
