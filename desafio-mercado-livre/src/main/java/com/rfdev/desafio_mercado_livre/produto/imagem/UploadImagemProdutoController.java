package com.rfdev.desafio_mercado_livre.produto.imagem;

import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.produto.imagem.UploadImagemProdutoResponse.ImagemUploadada;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@ConditionalOnProperty(name = "app.minio.enabled", havingValue = "true", matchIfMissing = true)
public class UploadImagemProdutoController {

    @Value("${app.minio.url}")
    private String minioUrl;

    @Value("${app.minio.bucket-name}")
    private String bucketName;

    private final ProdutoRepository produtoRepository;
    private final AsyncStorageService asyncStorageService;

    public UploadImagemProdutoController(
            ProdutoRepository produtoRepository,
            AsyncStorageService asyncStorageService) {
        this.produtoRepository = produtoRepository;
        this.asyncStorageService = asyncStorageService;
    }

    @PostMapping("/api/produtos/{id}/imagens")
    @Transactional
    public ResponseEntity<UploadImagemProdutoResponse> uploadImagens(
            @PathVariable UUID id,
            @RequestPart(name = "imagens") List<MultipartFile> imagens,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (!produto.pertenceAoUsuario(usuarioLogado)) {
            throw new AccessDeniedException("Você não tem permissão para adicionar imagens a este produto");
        }

        if (imagens == null || imagens.isEmpty()) {
            throw new IllegalArgumentException("É necessário enviar pelo menos uma imagem");
        }

        long arquivosValidos = imagens.stream().filter(arquivo -> !arquivo.isEmpty()).count();
        if (arquivosValidos == 0) {
            throw new IllegalArgumentException("É necessário enviar pelo menos uma imagem válida");
        }

        if (imagens.size() > 10) {
            throw new IllegalArgumentException("Número máximo de imagens é 10");
        }

        try {
            // Lê os bytes de todos os arquivos antes de disparar os uploads em paralelo
            List<CompletableFuture<String>> futures = new ArrayList<>();
            List<String> nomesOriginais = new ArrayList<>();

            for (MultipartFile arquivo : imagens) {
                if (arquivo.isEmpty()) {
                    continue;
                }
                nomesOriginais.add(arquivo.getOriginalFilename());
                futures.add(asyncStorageService.armazenarAsync(
                        arquivo.getOriginalFilename(),
                        arquivo.getBytes()));
            }

            // Aguarda todos os uploads terminarem em paralelo
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<String> urlsImagens = new ArrayList<>();
            List<ImagemUploadada> imagensUploadadas = new ArrayList<>();

            for (int i = 0; i < futures.size(); i++) {
                String nomeUnico = futures.get(i).get();
                String url = String.format("%s/%s/%s", minioUrl, bucketName, nomeUnico);
                urlsImagens.add(url);
                imagensUploadadas.add(new ImagemUploadada(url, nomeUnico));
            }

            produto.adicionarImagens(urlsImagens);
            produtoRepository.save(produto);

            return ResponseEntity.ok(UploadImagemProdutoResponse.of(produto.getId(), imagensUploadadas));

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException iae) {
                throw iae;
            }
            throw new RuntimeException("Erro ao fazer upload das imagens: " + e.getMessage());
        }
    }
}
