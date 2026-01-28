package com.rfdev.desafio_mercado_livre.produto.imagem;

import com.rfdev.desafio_mercado_livre.TestApi;
import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MinIOContainer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

class UploadImagemProdutoControllerTest extends TestApi {

    private static final MinIOContainer MINIO_CONTAINER = new MinIOContainer("minio/minio:RELEASE.2024-01-01T16-36-33Z")
            .withUserName("minioadmin")
            .withPassword("minioadmin");

    private static MinioClient minioClient;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProdutoRepository produtoRepository;

    private MockMvc mockMvc;

    @BeforeAll
    static void startMinIO() throws Exception {
        MINIO_CONTAINER.start();

        // Cria cliente MinIO
        minioClient = MinioClient.builder()
                .endpoint(MINIO_CONTAINER.getS3URL())
                .credentials(MINIO_CONTAINER.getUserName(), MINIO_CONTAINER.getPassword())
                .build();

        // Cria bucket "produtos"
        String bucketName = "produtos";
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build());

        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @AfterAll
    static void stopMinIO() {
        if (MINIO_CONTAINER != null) {
            MINIO_CONTAINER.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.minio.enabled", () -> "true");
        registry.add("app.minio.url", MINIO_CONTAINER::getS3URL);
        registry.add("app.minio.access-key", MINIO_CONTAINER::getUserName);
        registry.add("app.minio.secret-key", MINIO_CONTAINER::getPassword);
        registry.add("app.minio.bucket-name", () -> "produtos");
    }

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
    void deveUploadImagensComSucesso() throws Exception {
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

        // Cria arquivo de imagem válido
        MockMultipartFile imagem1 = new MockMultipartFile(
                "imagens",
                "notebook.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "conteudo da imagem 1".getBytes());

        MockMultipartFile imagem2 = new MockMultipartFile(
                "imagens",
                "notebook2.png",
                MediaType.IMAGE_PNG_VALUE,
                "conteudo da imagem 2".getBytes());

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId())
                                .file(imagem1)
                                .file(imagem2)
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.produtoId").value(produto.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imagens").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.imagens.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imagens[0].url").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.imagens[0].nomeArquivo").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.imagens[1].url").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.imagens[1].nomeArquivo").exists());

        // Verifica se as imagens foram salvas no banco
        entityManager.flush();
        entityManager.clear();

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(2, produtoAtualizado.getImagens().size());
        assertNotNull(produtoAtualizado.getImagens().get(0));
        assertNotNull(produtoAtualizado.getImagens().get(1));
    }

    @Test
    void deveRetornarErroQuandoProdutoNaoEncontrado() throws Exception {
        // Cria um usuário vendedor
        Usuario vendedor = new Usuario("vendedor@email.com", "SenhaForte123!");
        entityManager.persist(vendedor);
        entityManager.flush();

        MockMultipartFile imagem = new MockMultipartFile(
                "imagens",
                "notebook.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "conteudo da imagem".getBytes());

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", "550e8400-e29b-41d4-a716-446655440000")
                                .file(imagem)
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deveRetornarErroQuandoUsuarioNaoEhDonoDoProduto() throws Exception {
        // Cria uma categoria
        Categoria categoria = new Categoria("Eletrônicos", null);
        entityManager.persist(categoria);
        entityManager.flush();

        // Cria um usuário vendedor (dono do produto)
        Usuario vendedorDono = new Usuario("dono@email.com", "SenhaForte123!");
        entityManager.persist(vendedorDono);
        entityManager.flush();

        // Cria um produto
        Produto produto = new Produto(
                "Notebook ABC",
                new BigDecimal("3000.00"),
                BigInteger.valueOf(5),
                List.of("16GB RAM", "SSD 512GB", "Intel i7"),
                "Notebook de alta performance",
                categoria,
                vendedorDono);
        entityManager.persist(produto);
        entityManager.flush();

        // Cria outro usuário (não é dono)
        Usuario outroVendedor = new Usuario("outro@email.com", "SenhaForte123!");
        entityManager.persist(outroVendedor);
        entityManager.flush();

        MockMultipartFile imagem = new MockMultipartFile(
                "imagens",
                "notebook.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "conteudo da imagem".getBytes());

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId())
                                .file(imagem)
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(outroVendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deveRetornarErroQuandoNenhumaImagemEnviada() throws Exception {
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

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId())
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveRetornarErroQuandoImagemVazia() throws Exception {
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

        // Cria arquivo vazio
        MockMultipartFile imagemVazia = new MockMultipartFile(
                "imagens",
                "notebook.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]);

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId())
                                .file(imagemVazia)
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveRetornarErroQuandoMaisDe10Imagens() throws Exception {
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

        // Cria 11 imagens
        var multipartRequest = MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId());
        for (int i = 0; i < 11; i++) {
            MockMultipartFile imagem = new MockMultipartFile(
                    "imagens",
                    "imagem" + i + ".jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    ("conteudo da imagem " + i).getBytes());
            multipartRequest.file(imagem);
        }

        mockMvc.perform(
                        multipartRequest
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveRetornarErroQuandoTipoDeArquivoInvalido() throws Exception {
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

        // Cria arquivo PDF (não é imagem)
        MockMultipartFile arquivoPdf = new MockMultipartFile(
                "imagens",
                "documento.pdf",
                "application/pdf",
                "conteudo do pdf".getBytes());

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId())
                                .file(arquivoPdf)
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveRetornarErroQuandoArquivoMuitoGrande() throws Exception {
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

        // Cria arquivo maior que 5MB
        byte[] conteudoGrande = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile imagemGrande = new MockMultipartFile(
                "imagens",
                "imagem_grande.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                conteudoGrande);

        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId())
                                    .file(imagemGrande)
                                    .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                    .with(request -> {
                                        request.setMethod("POST");
                                        return request;
                                    }))
                    .andExpect(MockMvcResultMatchers.status().is5xxServerError());
        } catch (Exception e) {
            // O erro pode ser lançado como ServletException quando o arquivo é muito grande
            // Verifica se a causa raiz contém a mensagem de arquivo muito grande
            assertTrue(e.getMessage().contains("Erro ao fazer upload das imagens") ||
                    e.getCause() != null && e.getCause().getMessage().contains("Arquivo muito grande"));
        }
    }

    @Test
    void deveAceitarDiferentesTiposDeImagem() throws Exception {
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

        // Testa diferentes tipos de imagem
        MockMultipartFile imagemJpg = new MockMultipartFile(
                "imagens",
                "foto.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "conteudo jpg".getBytes());

        MockMultipartFile imagemPng = new MockMultipartFile(
                "imagens",
                "foto.png",
                MediaType.IMAGE_PNG_VALUE,
                "conteudo png".getBytes());

        MockMultipartFile imagemGif = new MockMultipartFile(
                "imagens",
                "foto.gif",
                MediaType.IMAGE_GIF_VALUE,
                "conteudo gif".getBytes());

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId())
                                .file(imagemJpg)
                                .file(imagemPng)
                                .file(imagemGif)
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.imagens.length()").value(3));

        // Verifica se as imagens foram salvas no banco
        entityManager.flush();
        entityManager.clear();

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(3, produtoAtualizado.getImagens().size());
    }

    @Test
    void devePermitirUploadDeAte10Imagens() throws Exception {
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

        // Cria exatamente 10 imagens
        var multipartRequest = MockMvcRequestBuilders.multipart("/api/produtos/{id}/imagens", produto.getId());
        for (int i = 0; i < 10; i++) {
            MockMultipartFile imagem = new MockMultipartFile(
                    "imagens",
                    "imagem" + i + ".jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    ("conteudo da imagem " + i).getBytes());
            multipartRequest.file(imagem);
        }

        mockMvc.perform(
                        multipartRequest
                                .with(SecurityMockMvcRequestPostProcessors.authentication(createAuthentication(vendedor)))
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.imagens.length()").value(10));

        // Verifica se as imagens foram salvas no banco
        entityManager.flush();
        entityManager.clear();

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(10, produtoAtualizado.getImagens().size());
    }
}