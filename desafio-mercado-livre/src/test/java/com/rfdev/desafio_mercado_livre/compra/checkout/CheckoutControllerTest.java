package com.rfdev.desafio_mercado_livre.compra.checkout;

import com.rfdev.desafio_mercado_livre.TestApi;
import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
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
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

class CheckoutControllerTest extends TestApi {

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
    void deveEnviarErroQuandoProdutoNaoForEncontrado() throws Exception {
        // Cria um usuário comprador
        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        // Cria um request com UUID inválido
        CheckoutRequest request = new CheckoutRequest(
                UUID.randomUUID(),
                BigInteger.valueOf(1),
                "PAYPAL"
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/checkout")
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(comprador)))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Produto não encontrado."));
    }

    @Test
    void deveEnviarErroQuandoEstoqueForInsuficiente() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        // Cria um produto com estoque limitado
        Produto produto = new Produto(
                "Smartphone XYZ",
                new BigDecimal("1500.00"),
                BigInteger.valueOf(5), // Estoque de apenas 5 unidades
                List.of("128GB", "5G", "Câmera 48MP"),
                "Smartphone com ótima câmera",
                categoria,
                vendedor);
        entityManager.persist(produto);
        entityManager.flush();

        // Cria um usuário comprador
        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        // Tenta comprar mais do que há em estoque
        CheckoutRequest request = new CheckoutRequest(
                produto.getId(),
                BigInteger.valueOf(10), // Quantidade maior que o estoque
                "PAYPAL"
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/checkout")
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(comprador)))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Estoque insuficiente para abater a quantidade solicitada."));
    }

    @Test
    void deveFinalizarCompraComSucesso() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        // Cria um produto com estoque suficiente
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

        // Cria um usuário comprador
        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        // Realiza a compra
        CheckoutRequest request = new CheckoutRequest(
                produto.getId(),
                BigInteger.valueOf(2),
                "PAYPAL"
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/checkout")
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(comprador)))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("paypal.com/")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("redirectUrl=")));

        entityManager.flush();
        entityManager.clear();

        // Verifica se a compra foi criada no banco
        Long compraCount = entityManager
                .createQuery("SELECT COUNT(c) FROM Compra c WHERE c.comprador.id = :compradorId", Long.class)
                .setParameter("compradorId", comprador.getId())
                .getSingleResult();

        assertEquals(1L, compraCount);

        // Verifica se o estoque foi abatido
        Produto produtoAtualizado = entityManager.find(Produto.class, produto.getId());
        assertNotNull(produtoAtualizado);
        assertEquals(BigInteger.valueOf(8), produtoAtualizado.getQuantidadeDisponivel());
    }
}