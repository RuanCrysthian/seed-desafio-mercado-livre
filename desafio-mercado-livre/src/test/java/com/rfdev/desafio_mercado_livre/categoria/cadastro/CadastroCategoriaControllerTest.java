package com.rfdev.desafio_mercado_livre.categoria.cadastro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.rfdev.desafio_mercado_livre.TestApi;
import com.rfdev.desafio_mercado_livre.categoria.Categoria;

import tools.jackson.databind.ObjectMapper;

public class CadastroCategoriaControllerTest extends TestApi {

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
    void deveCadastrarCategoriaSemCategoriaMae() throws Exception {
        CadastroCategoriaRequest request = new CadastroCategoriaRequest("Eletrônicos", null);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Eletrônicos"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nomeCategoriaMae").doesNotExist());

        entityManager.flush();
        entityManager.clear();

        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM Categoria c WHERE c.nome = :nome", Long.class)
                .setParameter("nome", "Eletrônicos")
                .getSingleResult();

        assertEquals(1L, count);

        Categoria categoria = entityManager
                .createQuery("SELECT c FROM Categoria c WHERE c.nome = :nome", Categoria.class)
                .setParameter("nome", "Eletrônicos")
                .getSingleResult();

        assertNull(categoria.getCategoriaMae());
    }

    @Test
    void deveCadastrarCategoriaComCategoriaMae() throws Exception {
        // Primeiro, cadastra a categoria mãe
        CadastroCategoriaRequest categoriaMaeRequest = new CadastroCategoriaRequest("Eletrônicos", null);

        String response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(categoriaMaeRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Eletrônicos"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nomeCategoriaMae").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CadastroCategoriaResponse categoriaMaeResponse = objectMapper.readValue(response,
                CadastroCategoriaResponse.class);

        entityManager.flush();
        entityManager.clear();

        // Agora cadastra a categoria filha
        CadastroCategoriaRequest categoriaFilhaRequest = new CadastroCategoriaRequest(
                "Celulares",
                categoriaMaeResponse.id());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(categoriaFilhaRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Celulares"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nomeCategoriaMae").value("Eletrônicos"));

        entityManager.flush();
        entityManager.clear();

        Categoria categoriaFilha = entityManager
                .createQuery("SELECT c FROM Categoria c WHERE c.nome = :nome", Categoria.class)
                .setParameter("nome", "Celulares")
                .getSingleResult();

        assertNotNull(categoriaFilha.getCategoriaMae());
        assertEquals("Eletrônicos", categoriaFilha.getCategoriaMae().getNome());
    }

    @Test
    void naoDeveCadastrarCategoriaComCategoriaMaeInexistente() throws Exception {
        UUID categoriaInexistenteId = UUID.randomUUID();
        CadastroCategoriaRequest request = new CadastroCategoriaRequest("Celulares", categoriaInexistenteId);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Categoria mãe não encontrada."));

        entityManager.flush();
        entityManager.clear();

        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM Categoria c WHERE c.nome = :nome", Long.class)
                .setParameter("nome", "Celulares")
                .getSingleResult();

        assertEquals(0L, count);
    }

    @Test
    void naoDeveCadastrarCategoriaComNomeDuplicadoNaMesmaCategoriaMae() throws Exception {
        // Primeiro, cadastra a categoria mãe
        CadastroCategoriaRequest categoriaMaeRequest = new CadastroCategoriaRequest("Eletrônicos", null);

        String responseMae = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(categoriaMaeRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Eletrônicos"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nomeCategoriaMae").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CadastroCategoriaResponse categoriaMaeResponse = objectMapper.readValue(responseMae,
                CadastroCategoriaResponse.class);

        entityManager.flush();
        entityManager.clear();

        // Cadastra a primeira categoria filha
        CadastroCategoriaRequest primeiraFilhaRequest = new CadastroCategoriaRequest(
                "Celulares",
                categoriaMaeResponse.id());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(primeiraFilhaRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Celulares"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nomeCategoriaMae").value("Eletrônicos"));

        entityManager.flush();
        entityManager.clear();

        // Tenta cadastrar outra categoria com o mesmo nome na mesma categoria mãe
        CadastroCategoriaRequest segundaFilhaRequest = new CadastroCategoriaRequest(
                "Celulares",
                categoriaMaeResponse.id());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(segundaFilhaRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]")
                        .value("Nome já cadastrado no sistema."));

        entityManager.flush();
        entityManager.clear();

        // Verifica que apenas uma categoria "Celulares" foi cadastrada
        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM Categoria c WHERE c.nome = :nome", Long.class)
                .setParameter("nome", "Celulares")
                .getSingleResult();

        assertEquals(1L, count);
    }

    @Test
    void naoDeveCadastrarCategoriaSemNome() throws Exception {
        CadastroCategoriaRequest request = new CadastroCategoriaRequest("", null);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagens[0]").value("Nome é obrigatório."));

        entityManager.flush();
        entityManager.clear();

        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM Categoria c", Long.class)
                .getSingleResult();

        assertEquals(0L, count);
    }
}
