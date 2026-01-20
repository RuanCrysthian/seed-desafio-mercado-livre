package com.rfdev.desafio_mercado_livre.produto.imagem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.ObjectStorage;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.produto.imagem.UploadImagemProdutoResponse.ImagemUploadada;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.persistence.EntityNotFoundException;

@RestController
public class UploadImagemProdutoController {

    @Value("${app.minio.url}")
    private String minioUrl;

    @Value("${app.minio.bucket-name}")
    private String bucketName;

    private final ProdutoRepository produtoRepository;
    private final ObjectStorage objectStorage;

    public UploadImagemProdutoController(
            ProdutoRepository produtoRepository,
            ObjectStorage objectStorage) {
        this.produtoRepository = produtoRepository;
        this.objectStorage = objectStorage;
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

        // Validações
        if (imagens == null || imagens.isEmpty()) {
            throw new IllegalArgumentException("É necessário enviar pelo menos uma imagem");
        }

        // Valida se há pelo menos um arquivo não vazio
        long arquivosValidos = imagens.stream().filter(arquivo -> !arquivo.isEmpty()).count();
        if (arquivosValidos == 0) {
            throw new IllegalArgumentException("É necessário enviar pelo menos uma imagem válida");
        }

        if (imagens.size() > 10) {
            throw new IllegalArgumentException("Número máximo de imagens é 10");
        }

        try {
            List<ImagemUploadada> imagensUploadadas = new ArrayList<>();
            List<String> urlsImagens = new ArrayList<>();

            for (MultipartFile arquivo : imagens) {
                if (arquivo.isEmpty()) {
                    continue;
                }

                String nomeArquivo = objectStorage.armazenar(
                        arquivo.getOriginalFilename(),
                        arquivo.getInputStream());

                String url = String.format("%s/%s/%s", minioUrl, bucketName, nomeArquivo);

                produto.adicionarImagens(urlsImagens);
                urlsImagens.add(url);

                imagensUploadadas.add(new ImagemUploadada(
                        url,
                        nomeArquivo));
            }

            produtoRepository.save(produto);

            UploadImagemProdutoResponse response = UploadImagemProdutoResponse.of(
                    produto.getId(),
                    imagensUploadadas);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload das imagens: " + e.getMessage());
        }
    }
}
