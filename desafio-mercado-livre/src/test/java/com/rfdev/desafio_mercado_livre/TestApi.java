package com.rfdev.desafio_mercado_livre;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public abstract class TestApi {

    static {
        DatabaseTest.isRunning();
    }

    @PersistenceContext
    protected EntityManager entityManager;

    @AfterEach
    void cleanDatabase() {
        entityManager.createNativeQuery("DELETE FROM opinioes").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM perguntas").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM produto_imagens").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM produto_caracteristicas").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM produtos").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM categorias").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM usuarios").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }
}
