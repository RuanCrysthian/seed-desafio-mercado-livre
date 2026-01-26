package com.rfdev.desafio_mercado_livre.opiniao.cadastro;

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
import com.rfdev.desafio_mercado_livre.opiniao.Opiniao;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import tools.jackson.databind.ObjectMapper;

public class CadastroOpiniaoControllerTest extends TestApi {

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
    void deveCadastrarOpiniaoComSucesso() throws Exception {
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

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                5,
                "Excelente produto",
                "Comprei e adorei, recomendo muito!",
                produto.getId());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nota").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value("Excelente produto"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao").value("Comprei e adorei, recomendo muito!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.consumidorLogin").value("consumidor@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.produtoNome").value("Smartphone XYZ"));

        entityManager.flush();
        entityManager.clear();

        Long count = entityManager
                .createQuery("SELECT COUNT(o) FROM Opiniao o WHERE o.titulo = :titulo", Long.class)
                .setParameter("titulo", "Excelente produto")
                .getSingleResult();

        assertEquals(1L, count);
    }

    @Test
    void deveRetornarErroQuandoNotaNula() throws Exception {
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

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                null, // Nota nula
                "Bom produto",
                "Gostei muito",
                produto.getId());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Nota é obrigatória."));
    }

    @Test
    void deveRetornarErroQuandoNotaMenorQueUm() throws Exception {
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

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                0, // Nota menor que 1
                "Produto ruim",
                "Não gostei",
                produto.getId());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Nota mínima é 1."));
    }

    @Test
    void deveRetornarErroQuandoNotaMaiorQueCinco() throws Exception {
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

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                6, // Nota maior que 5
                "Produto excelente",
                "Adorei demais",
                produto.getId());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Nota máxima é 5."));
    }

    @Test
    void deveRetornarErroQuandoTituloEmBranco() throws Exception {
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

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                5,
                "", // Título em branco
                "Produto muito bom",
                produto.getId());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Título é obrigatório."));
    }

    @Test
    void deveRetornarErroQuandoDescricaoEmBranco() throws Exception {
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

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                5,
                "Bom produto",
                "", // Descrição em branco
                produto.getId());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Descrição é obrigatória."));
    }

    @Test
    void deveRetornarErroQuandoDescricaoMaiorQueQuinhentosCaracteres() throws Exception {
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

        // Cria uma descrição com mais de 500 caracteres
        String descricaoLonga = "A".repeat(501);

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                5,
                "Ótimo produto",
                descricaoLonga,
                produto.getId());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                        .value("Descrição deve ter no máximo 500 caracteres."));
    }

    @Test
    void deveRetornarErroQuandoProdutoNaoExistir() throws Exception {
        // Cria um usuário consumidor
        Usuario consumidor = new Usuario("consumidor@email.com", "SenhaForte123!");
        entityManager.persist(consumidor);
        entityManager.flush();

        UUID produtoInexistente = UUID.randomUUID();

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                5,
                "Bom produto",
                "Gostei muito",
                produtoInexistente);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray());
    }

    @Test
    void deveRetornarErroQuandoProdutoIdNulo() throws Exception {
        // Cria um usuário consumidor
        Usuario consumidor = new Usuario("consumidor@email.com", "SenhaForte123!");
        entityManager.persist(consumidor);
        entityManager.flush();

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                5,
                "Bom produto",
                "Gostei muito",
                null); // ProdutoId nulo

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Produto é obrigatório."));
    }

    @Test
    void deveAssociarOpiniaoAoUsuarioLogado() throws Exception {
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

        CadastroOpiniaoRequest request = new CadastroOpiniaoRequest(
                5,
                "Excelente produto",
                "Comprei e adorei, recomendo muito!",
                produto.getId());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/opinioes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(consumidor)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        entityManager.flush();
        entityManager.clear();

        Opiniao opiniao = entityManager
                .createQuery("SELECT o FROM Opiniao o WHERE o.titulo = :titulo", Opiniao.class)
                .setParameter("titulo", "Excelente produto")
                .getSingleResult();

        assertNotNull(opiniao.getConsumidor());
        assertEquals(consumidor.getId(), opiniao.getConsumidor().getId());
        assertEquals(consumidor.getLogin(), opiniao.getConsumidor().getLogin());
    }
}
