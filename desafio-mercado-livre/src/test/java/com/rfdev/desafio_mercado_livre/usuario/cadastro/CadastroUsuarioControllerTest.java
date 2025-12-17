package com.rfdev.desafio_mercado_livre.usuario.cadastro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.rfdev.desafio_mercado_livre.TestApi;

import tools.jackson.databind.ObjectMapper;

public class CadastroUsuarioControllerTest extends TestApi {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void deveCadastrarUsuarioComDadosValidos() throws Exception {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "john.doe@email.com",
                "SenhaForte123!");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        entityManager.flush();
        entityManager.clear();

        Long count = entityManager.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.login = :login", Long.class)
                .setParameter("login", "john.doe@email.com")
                .getSingleResult();

        assertEquals(1L, count);
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailJaCadastrado() throws Exception {
        CadastroUsuarioRequest primeiroRequest = new CadastroUsuarioRequest(
                "duplicado@email.com",
                "SenhaForte123!");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(primeiroRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("duplicado@email.com"));

        entityManager.flush();
        entityManager.clear();

        // Segundo cadastro com mesmo email - deve falhar
        CadastroUsuarioRequest segundoRequest = new CadastroUsuarioRequest(
                "duplicado@email.com",
                "OutraSenhaForte456!");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(segundoRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Email já cadastrado."));
        ;

        // Verifica que apenas um usuário foi cadastrado
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.login = :login", Long.class)
                .setParameter("login", "duplicado@email.com")
                .getSingleResult();

        assertEquals(1L, count);
    }

    @Test
    void naoDeveCadastrarUsuarioComSenhaFraca() throws Exception {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "usuario@email.com",
                "123");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Senha fraca."));
    }

    @Test
    void naoDeveCadastrarUsuarioComDadosInvalidos() throws Exception {

        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "email-invalido",
                "12345");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens",
                        Matchers.containsInAnyOrder("Email inválido.", "Senha fraca.")));
    }
}
