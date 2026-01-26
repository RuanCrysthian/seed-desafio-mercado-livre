package com.rfdev.desafio_mercado_livre.produto.detalhe;

import com.rfdev.desafio_mercado_livre.TestApi;
import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.opiniao.Opiniao;
import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

class DetalheProdutoControllerTest extends TestApi {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void deveRetornarDetalhesDoProduto() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário
        Usuario usuario = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(usuario);
        entityManager.flush();

        // Cria um produto
        Produto produto = new Produto(
                "Smartphone XYZ",
                new BigDecimal("1500.00"),
                BigInteger.valueOf(10),
                List.of("128GB", "5G", "Câmera 48MP"),
                "Smartphone com ótima câmera e bateria de longa duração",
                categoria,
                usuario);
        entityManager.persist(produto);
        entityManager.flush();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/produtos/{produtoId}", produto.getId())
                                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Smartphone XYZ"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao")
                        .value("Smartphone com ótima câmera e bateria de longa duração"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(1500.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$.caracteristicas").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.caracteristicas.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.linksImagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mediaNotas").value(0.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalNotas").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes.length()").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.perguntas").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.perguntas.length()").value(0));
    }

    @Test
    void deveLancarExcecaoParaProdutoInexistente() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/produtos/{produtoId}", "00000000-0000-0000-0000-000000000000")
                                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deveRetornarDetalhesDoProdutoComOpinioes() throws Exception {
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
                "Notebook ABC",
                new BigDecimal("3000.00"),
                BigInteger.valueOf(5),
                List.of("16GB RAM", "SSD 512GB", "Intel i7"),
                "Notebook de alta performance",
                categoria,
                vendedor);
        entityManager.persist(produto);
        entityManager.flush();

        // Cria um usuário comprador
        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        // Cria opiniões
        Opiniao opiniao1 = new Opiniao(5, "Excelente", "Produto de altíssima qualidade", comprador, produto);
        Opiniao opiniao2 = new Opiniao(4, "Muito bom", "Vale a pena", comprador, produto);
        entityManager.persist(opiniao1);
        entityManager.persist(opiniao2);
        entityManager.flush();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/produtos/{produtoId}", produto.getId())
                                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Notebook ABC"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mediaNotas").value(4.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalNotas").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes[0].nota").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes[0].titulo").value("Excelente"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes[0].descricao")
                        .value("Produto de altíssima qualidade"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes[1].nota").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes[1].titulo").value("Muito bom"));
    }

    @Test
    void deveRetornarDetalhesDoProdutoComPerguntas() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Livros", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        // Cria um produto
        Produto produto = new Produto(
                "Livro de Java",
                new BigDecimal("80.00"),
                BigInteger.valueOf(20),
                List.of("500 páginas", "Edição 2024", "Capa dura"),
                "Livro completo sobre Java",
                categoria,
                vendedor);
        entityManager.persist(produto);
        entityManager.flush();

        // Cria um usuário interessado
        Usuario interessado = new Usuario("interessado@email.com", "SenhaForte123!");
        entityManager.persist(interessado);
        entityManager.flush();

        // Cria perguntas
        Pergunta pergunta1 = new Pergunta("Qual a editora?", produto, interessado);
        Pergunta pergunta2 = new Pergunta("É atualizado com Java 17?", produto, interessado);
        entityManager.persist(pergunta1);
        entityManager.persist(pergunta2);
        entityManager.flush();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/produtos/{produtoId}", produto.getId())
                                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Livro de Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.perguntas").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.perguntas.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.perguntas[0].titulo").value("Qual a editora?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.perguntas[1].titulo")
                        .value("É atualizado com Java 17?"));
    }

    @Test
    void deveRetornarDetalhesDoProdutoComOpinioesEPerguntas() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Games", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        // Cria um produto
        Produto produto = new Produto(
                "Console XYZ",
                new BigDecimal("2500.00"),
                BigInteger.valueOf(3),
                List.of("4K", "HDR", "2TB SSD"),
                "Console de última geração",
                categoria,
                vendedor);
        entityManager.persist(produto);
        entityManager.flush();

        // Cria usuários
        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        Usuario interessado = new Usuario("interessado@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.persist(interessado);
        entityManager.flush();

        // Cria opiniões
        Opiniao opiniao = new Opiniao(5, "Perfeito", "Melhor console do mercado", comprador, produto);
        entityManager.persist(opiniao);
        entityManager.flush();

        // Cria perguntas
        Pergunta pergunta = new Pergunta("Vem com controles?", produto, interessado);
        entityManager.persist(pergunta);
        entityManager.flush();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/produtos/{produtoId}", produto.getId())
                                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Console XYZ"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(2500.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mediaNotas").value(5.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalNotas").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.perguntas.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opinioes[0].titulo").value("Perfeito"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.perguntas[0].titulo").value("Vem com controles?"));
    }

}