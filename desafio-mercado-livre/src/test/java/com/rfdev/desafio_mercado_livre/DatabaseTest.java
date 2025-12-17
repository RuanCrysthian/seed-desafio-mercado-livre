package com.rfdev.desafio_mercado_livre;

import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseTest {

    private static final PostgreSQLContainer<?> CONTAINER;

    static {
        CONTAINER = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);

        CONTAINER.start();

        // Configura propriedades GLOBAIS
        System.setProperty("spring.datasource.url", CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", CONTAINER.getPassword());
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");
        System.setProperty("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect");
        System.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    }

    @AfterAll
    static void tearDown() {
        if (CONTAINER != null) {
            CONTAINER.stop();
        }
    }

    public static boolean isRunning() {
        return CONTAINER.isRunning();
    }
}
