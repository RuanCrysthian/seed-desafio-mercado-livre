package com.rfdev.desafio_mercado_livre.produto.cadastro;

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
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import tools.jackson.databind.ObjectMapper;

public class CadastroProdutoControllerTest extends TestApi {

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
        void deveCadastrarProdutoComSucesso() throws Exception {
                // Cria uma categoria para o produto
                Categoria categoria = new Categoria("Eletrônicos", null);
                entityManager.persist(categoria);
                entityManager.flush();

                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "Smartphone XYZ",
                                new BigDecimal("1500.00"),
                                BigInteger.valueOf(10),
                                List.of("128GB", "5G", "Câmera 48MP"),
                                "Smartphone com ótima câmera e bateria de longa duração",
                                categoria.getId());

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Smartphone XYZ"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.valor").value(1500.00))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.quantidadeDisponivel").value(10))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.caracteristicas").isArray())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.caracteristicas.length()").value(3))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao")
                                                .value("Smartphone com ótima câmera e bateria de longa duração"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.categoriaNome").value("Eletrônicos"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.criadoEm").exists());

                entityManager.flush();
                entityManager.clear();

                Long count = entityManager
                                .createQuery("SELECT COUNT(p) FROM Produto p WHERE p.nome = :nome", Long.class)
                                .setParameter("nome", "Smartphone XYZ")
                                .getSingleResult();

                assertEquals(1L, count);
        }

        @Test
        void deveRetornarErroAoCadastrarProdutoComValorInvalidos() throws Exception {
                // Cria uma categoria para o produto
                Categoria categoria = new Categoria("Eletrônicos", null);
                entityManager.persist(categoria);
                entityManager.flush();

                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "Smartphone XYZ",
                                new BigDecimal("-100.00"), // Valor negativo (inválido)
                                BigInteger.valueOf(10),
                                List.of("128GB", "5G", "Câmera 48MP"),
                                "Smartphone com ótima câmera",
                                categoria.getId());

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                                                .value("Valor deve ser maior que zero."));
        }

        @Test
        void deveRetornarErroAoCadastrarProdutoComQuantidadeDisponivelNegativa() throws Exception {
                // Cria uma categoria para o produto
                Categoria categoria = new Categoria("Eletrônicos", null);
                entityManager.persist(categoria);
                entityManager.flush();

                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "Smartphone XYZ",
                                new BigDecimal("1500.00"),
                                BigInteger.valueOf(-5), // Quantidade negativa (inválida)
                                List.of("128GB", "5G", "Câmera 48MP"),
                                "Smartphone com ótima câmera",
                                categoria.getId());

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                                                .value("Quantidade disponível deve ser maior ou igual a zero."));
        }

        @Test
        void deveRetornarErroAoCadastrarProdutoComMenosDeTresCaracteristicas() throws Exception {
                // Cria uma categoria para o produto
                Categoria categoria = new Categoria("Eletrônicos", null);
                entityManager.persist(categoria);
                entityManager.flush();

                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "Smartphone XYZ",
                                new BigDecimal("1500.00"),
                                BigInteger.valueOf(10),
                                List.of("128GB", "5G"), // Apenas 2 características (menos que 3)
                                "Smartphone com ótima câmera",
                                categoria.getId());

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                                                .value("Características deve ter pelo menos 3 itens."));
        }

        @Test
        void deveRetornarErroAoCadastrarProdutoComNomeEmBranco() throws Exception {
                // Cria uma categoria para o produto
                Categoria categoria = new Categoria("Eletrônicos", null);
                entityManager.persist(categoria);
                entityManager.flush();

                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "", // Nome em branco
                                new BigDecimal("1500.00"),
                                BigInteger.valueOf(10),
                                List.of("128GB", "5G", "Câmera 48MP"),
                                "Smartphone com ótima câmera",
                                categoria.getId());

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                                                .value("Nome é obrigatório."));
        }

        @Test
        void deveRetornarErroAoCadastrarProdutoComDescricaoEmBranco() throws Exception {
                // Cria uma categoria para o produto
                Categoria categoria = new Categoria("Eletrônicos", null);
                entityManager.persist(categoria);
                entityManager.flush();

                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "Smartphone XYZ",
                                new BigDecimal("1500.00"),
                                BigInteger.valueOf(10),
                                List.of("128GB", "5G", "Câmera 48MP"),
                                "", // Descrição em branco
                                categoria.getId());

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                                                .value("Descrição é obrigatória."));
        }

        @Test
        void deveRetornarErroAoCadastrarProdutoComDescricaoMaiorQueMilCaracteres() throws Exception {
                // Cria uma categoria para o produto
                Categoria categoria = new Categoria("Eletrônicos", null);
                entityManager.persist(categoria);
                entityManager.flush();

                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                // Cria uma descrição com mais de 1000 caracteres
                String descricaoLonga = "A".repeat(1001);

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "Smartphone XYZ",
                                new BigDecimal("1500.00"),
                                BigInteger.valueOf(10),
                                List.of("128GB", "5G", "Câmera 48MP"),
                                descricaoLonga,
                                categoria.getId());

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                                                .value("Descrição deve ter no máximo 1000 caracteres."));
        }

        @Test
        void deveRetornarErroQuandoCategoriaNaoExistir() throws Exception {
                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                UUID categoriaInexistente = UUID.randomUUID();

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "Smartphone XYZ",
                                new BigDecimal("1500.00"),
                                BigInteger.valueOf(10),
                                List.of("128GB", "5G", "Câmera 48MP"),
                                "Smartphone com ótima câmera",
                                categoriaInexistente);

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                                                .value("Categoria não existe"));
        }

        @Test
        void deveAssociarProdutoAoUsuarioLogado() throws Exception {
                // Cria uma categoria para o produto
                Categoria categoria = new Categoria("Eletrônicos", null);
                entityManager.persist(categoria);
                entityManager.flush();

                // Cria um usuário para o produto
                Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
                entityManager.persist(usuario);
                entityManager.flush();

                CadastroProdutoRequest request = new CadastroProdutoRequest(
                                "Smartphone XYZ",
                                new BigDecimal("1500.00"),
                                BigInteger.valueOf(10),
                                List.of("128GB", "5G", "Câmera 48MP"),
                                "Smartphone com ótima câmera",
                                categoria.getId());

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/produtos")
                                                .with(SecurityMockMvcRequestPostProcessors
                                                                .authentication(createAuthentication(usuario)))
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(MockMvcResultMatchers.status().isOk());

                entityManager.flush();
                entityManager.clear();

                Produto produto = entityManager
                                .createQuery("SELECT p FROM Produto p WHERE p.nome = :nome", Produto.class)
                                .setParameter("nome", "Smartphone XYZ")
                                .getSingleResult();

                assertNotNull(produto.getUsuarioCriador());
                assertEquals(usuario.getId(), produto.getUsuarioCriador().getId());
                assertEquals(usuario.getLogin(), produto.getUsuarioCriador().getLogin());
        }
}
