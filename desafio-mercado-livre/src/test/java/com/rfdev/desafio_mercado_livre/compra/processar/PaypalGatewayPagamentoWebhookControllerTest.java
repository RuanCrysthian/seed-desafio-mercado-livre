package com.rfdev.desafio_mercado_livre.compra.processar;

import com.rfdev.desafio_mercado_livre.TestApi;
import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.compra.*;
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
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaypalGatewayPagamentoWebhookControllerTest extends TestApi {

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
    void deveRetornarErroQuandoTentarCompraInexistente() throws Exception {
        // Cria um request com UUID de compra inexistente
        GatewayPagamentoWebhookRequest request = new GatewayPagamentoWebhookRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                1
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/retorno-pagamento-paypal")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Compra não encontrada"));
    }

    @Test
    void deveRetornarErroQuandoCompraJaConcluida() throws Exception {
        // Cria as entidades necessárias
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

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

        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        // Cria uma compra e a conclui
        Compra compra = Compra.criarPagamentoPaypal(produto, BigInteger.valueOf(2), comprador);
        compra.concluirCompra(); // Já conclui a compra
        entityManager.persist(compra);
        entityManager.flush();

        // Tenta processar novamente
        GatewayPagamentoWebhookRequest request = new GatewayPagamentoWebhookRequest(
                compra.getId(),
                UUID.randomUUID(),
                1
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/retorno-pagamento-paypal")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Compra já foi concluída com sucesso"));
    }

    @Test
    void deveRetornarErroQuandoTransacaoJaProcessada() throws Exception {
        // Cria as entidades necessárias
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

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

        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        Compra compra = Compra.criarPagamentoPaypal(produto, BigInteger.valueOf(2), comprador);
        entityManager.persist(compra);
        entityManager.flush();

        // Cria uma transação já processada
        UUID transacaoGatewayId = UUID.randomUUID();
        TransacaoPagamento transacao = new TransacaoPagamento(
                compra,
                transacaoGatewayId,
                PagamentoStatus.SUCESSO,
                TipoGatewayPagamento.PAYPAL);
        entityManager.persist(transacao);
        entityManager.flush();

        // Tenta processar novamente com o mesmo transacaoGatewayId
        GatewayPagamentoWebhookRequest request = new GatewayPagamentoWebhookRequest(
                compra.getId(),
                transacaoGatewayId,
                1
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/retorno-pagamento-paypal")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Transação já foi processada"));
    }

    @Test
    void deveRetornarErroQuandoJaExistirTransacaoComCompraIdEMTransacaoIdEGatewayIguais() throws Exception {
        // Cria as entidades necessárias
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

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

        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        Compra compra = Compra.criarPagamentoPaypal(produto, BigInteger.valueOf(2), comprador);
        entityManager.persist(compra);
        entityManager.flush();

        // Cria uma transação para esta compra no gateway PayPal
        TransacaoPagamento transacao = new TransacaoPagamento(
                compra,
                UUID.randomUUID(),
                PagamentoStatus.FALHA,
                TipoGatewayPagamento.PAYPAL);
        entityManager.persist(transacao);
        entityManager.flush();

        // Tenta processar novamente para a mesma compra no mesmo gateway (mesmo que com transacaoGatewayId diferente)
        GatewayPagamentoWebhookRequest request = new GatewayPagamentoWebhookRequest(
                compra.getId(),
                UUID.randomUUID(), // Transação diferente, mas mesma compra e gateway
                1
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/retorno-pagamento-paypal")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Já existe transação para esta compra neste gateway"));
    }

    @Test
    void deveProcessarTransacaoComStatusFalha() throws Exception {
        // Cria as entidades necessárias
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

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

        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        Compra compra = Compra.criarPagamentoPaypal(produto, BigInteger.valueOf(2), comprador);
        entityManager.persist(compra);
        entityManager.flush();

        // Processa transação com status de falha (0 no PayPal)
        GatewayPagamentoWebhookRequest request = new GatewayPagamentoWebhookRequest(
                compra.getId(),
                UUID.randomUUID(),
                0
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/retorno-pagamento-paypal")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("FALHA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataPagamento").exists());

        entityManager.flush();
        entityManager.clear();

        // Verifica que a transação foi registrada
        Long transacaoCount = entityManager
                .createQuery("SELECT COUNT(t) FROM TransacaoPagamento t WHERE t.compraOrigem.id = :compraId", Long.class)
                .setParameter("compraId", compra.getId())
                .getSingleResult();

        assertEquals(1L, transacaoCount);

        // Verifica que a compra NÃO foi concluída
        Compra compraAtualizada = entityManager.find(Compra.class, compra.getId());
        assertNotNull(compraAtualizada);
        assertEquals(CompraStatus.INICIADA, compraAtualizada.getStatus());
    }

    @Test
    void deveProcessarTransacaoComStatusSucesso() throws Exception {
        // Cria as entidades necessárias
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

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

        Usuario comprador = new Usuario("comprador@email.com", "SenhaForte123!");
        entityManager.persist(comprador);
        entityManager.flush();

        Compra compra = Compra.criarPagamentoPaypal(produto, BigInteger.valueOf(2), comprador);
        entityManager.persist(compra);
        entityManager.flush();

        // Processa transação com status de sucesso (1 no PayPal)
        GatewayPagamentoWebhookRequest request = new GatewayPagamentoWebhookRequest(
                compra.getId(),
                UUID.randomUUID(),
                1
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/retorno-pagamento-paypal")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCESSO"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataPagamento").exists());

        entityManager.flush();
        entityManager.clear();

        // Verifica que a transação foi registrada
        Long transacaoCount = entityManager
                .createQuery("SELECT COUNT(t) FROM TransacaoPagamento t WHERE t.compraOrigem.id = :compraId", Long.class)
                .setParameter("compraId", compra.getId())
                .getSingleResult();

        assertEquals(1L, transacaoCount);

        // Verifica que a compra foi concluída
        Compra compraAtualizada = entityManager.find(Compra.class, compra.getId());
        assertNotNull(compraAtualizada);
        assertEquals(CompraStatus.CONCLUIDA, compraAtualizada.getStatus());
    }

}

