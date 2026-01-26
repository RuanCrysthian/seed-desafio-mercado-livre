package com.rfdev.desafio_mercado_livre.pergunta.cadastro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.rfdev.desafio_mercado_livre.TestApi;
import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import tools.jackson.databind.ObjectMapper;

public class CadastroPerguntaControllerTest extends TestApi {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    private UsernamePasswordAuthenticationToken createAuthentication(Usuario usuario) {
        return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    }

    @Test
    void deveCadastrarPerguntaComSucesso() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        // Cria um produto
        Produto produto = new Produto(
                "Smartphone XYZ",
                new BigDecimal("1500.00"),
                BigInteger.valueOf(10),
                List.of("128GB", "5G", "Câmera 48MP"),
                "Smartphone com ótima câmera",
                categoria,
                vendedor);
        entityManager.persist(produto);
        entityManager.flush();

        // Cria um usuário consumidor
        Usuario consumidor = new Usuario("consumidor@email.com", "SenhaForte123!");
        entityManager.persist(consumidor);
        entityManager.flush();

        CadastroPerguntaRequest request = new CadastroPerguntaRequest(
                "Qual é a capacidade da bateria?");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/" + produto.getId() + "/perguntas")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].titulo").value("Qual é a capacidade da bateria?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nomeProduto").value("Smartphone XYZ"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].nomeUsuarioAutorPergunta").value("consumidor@email.com"));

        entityManager.flush();
        entityManager.clear();

        Long count = entityManager
                .createQuery("SELECT COUNT(p) FROM Pergunta p WHERE p.titulo = :titulo", Long.class)
                .setParameter("titulo", "Qual é a capacidade da bateria?")
                .getSingleResult();

        assertEquals(1L, count);
    }

    @Test
    void deveRetornarErroQuandoTituloPerguntaEstiverVazio() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        // Cria um produto
        Produto produto = new Produto(
                "Smartphone XYZ",
                new BigDecimal("1500.00"),
                BigInteger.valueOf(10),
                List.of("128GB", "5G", "Câmera 48MP"),
                "Smartphone com ótima câmera",
                categoria,
                vendedor);
        entityManager.persist(produto);
        entityManager.flush();

        // Cria um usuário consumidor
        Usuario consumidor = new Usuario("consumidor@email.com", "SenhaForte123!");
        entityManager.persist(consumidor);
        entityManager.flush();

        CadastroPerguntaRequest request = new CadastroPerguntaRequest(
                ""); // Título vazio

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/" + produto.getId() + "/perguntas")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Título é obrigatório."));
    }

    @Test
    void deveRetornarErroQuandoProdutoNaoExistir() throws Exception {
        // Cria um usuário consumidor
        Usuario consumidor = new Usuario("consumidor@email.com", "SenhaForte123!");
        entityManager.persist(consumidor);
        entityManager.flush();

        UUID produtoInexistente = UUID.randomUUID();

        CadastroPerguntaRequest request = new CadastroPerguntaRequest(
                "Qual é a garantia?");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/" + produtoInexistente + "/perguntas")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray());
    }

    @Test
    void deveRetornarErroQuandoUsuarioNaoEstiverLogado() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        // Cria um produto
        Produto produto = new Produto(
                "Smartphone XYZ",
                new BigDecimal("1500.00"),
                BigInteger.valueOf(10),
                List.of("128GB", "5G", "Câmera 48MP"),
                "Smartphone com ótima câmera",
                categoria,
                vendedor);
        entityManager.persist(produto);
        entityManager.flush();

        CadastroPerguntaRequest request = new CadastroPerguntaRequest(
                "Qual é a capacidade da bateria?");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/" + produto.getId() + "/perguntas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deveEnviarEmailAoCadastrarPergunta() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        // Cria um produto
        Produto produto = new Produto(
                "Smartphone XYZ",
                new BigDecimal("1500.00"),
                BigInteger.valueOf(10),
                List.of("128GB", "5G", "Câmera 48MP"),
                "Smartphone com ótima câmera",
                categoria,
                vendedor);
        entityManager.persist(produto);
        entityManager.flush();

        // Cria um usuário consumidor
        Usuario consumidor = new Usuario("consumidor@email.com", "SenhaForte123!");
        entityManager.persist(consumidor);
        entityManager.flush();

        CadastroPerguntaRequest request = new CadastroPerguntaRequest(
                "Qual é a capacidade da bateria?");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/" + produto.getId() + "/perguntas")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        entityManager.flush();
        entityManager.clear();

        Pergunta pergunta = entityManager
                .createQuery("SELECT p FROM Pergunta p WHERE p.titulo = :titulo", Pergunta.class)
                .setParameter("titulo", "Qual é a capacidade da bateria?")
                .getSingleResult();

        assertNotNull(pergunta);
        assertNotNull(pergunta.getUsuarioAutorPergunta());
        assertEquals(consumidor.getId(), pergunta.getUsuarioAutorPergunta().getId());
        assertEquals(produto.getId(), pergunta.getProduto().getId());
    }
}
