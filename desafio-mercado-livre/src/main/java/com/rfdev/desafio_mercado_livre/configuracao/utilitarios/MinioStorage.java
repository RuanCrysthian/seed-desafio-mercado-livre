package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

public class MinioStorage implements ObjectStorage {

    private static final Logger logger = LoggerFactory.getLogger(MinioStorage.class);

    // Tipos de arquivo aceitos para imagens
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp");

    // Tamanho máximo: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final MinioClient minioClient;
    private final String minioBucketName;

    public MinioStorage(MinioClient minioClient, String minioBucketName) {
        this.minioClient = minioClient;
        this.minioBucketName = minioBucketName;
    }

    @Override
    public String armazenar(String nomeArquivo, InputStream fileContent) throws Exception {
        // Sanitiza o nome do arquivo removendo path traversal e caracteres especiais
        String nomeSeguro = sanitizarNomeArquivo(nomeArquivo);

        // Detecta o content type baseado na extensão do arquivo
        String contentType = detectarContentType(nomeSeguro);

        // Valida o content type
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            logger.warn("Tentativa de upload de arquivo não permitido: {}", contentType);
            throw new IllegalArgumentException(
                    "Tipo de arquivo não permitido. Apenas imagens são aceitas (JPEG, PNG, GIF, WebP).");
        }

        // Gera nome único para o arquivo
        String nomeUnico = gerarNomeUnico(nomeSeguro);

        try {
            byte[] bytes = fileContent.readAllBytes();

            // Valida o tamanho do arquivo
            if (bytes.length > MAX_FILE_SIZE) {
                logger.warn("Tentativa de upload de arquivo muito grande: {} bytes", bytes.length);
                throw new IllegalArgumentException(
                        String.format("Arquivo muito grande. Tamanho máximo: %d MB", MAX_FILE_SIZE / (1024 * 1024)));
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(nomeUnico)
                            .stream(new java.io.ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType(contentType)
                            .build());

            logger.info("Arquivo armazenado com sucesso: {} (tamanho: {} bytes)", nomeUnico, bytes.length);
            return nomeUnico;

        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo: {}", nomeArquivo, e);
            throw new RuntimeException("Erro ao processar o arquivo", e);
        } catch (Exception e) {
            logger.error("Erro ao armazenar arquivo no MinIO: {}", nomeArquivo, e);
            throw new RuntimeException("Erro ao armazenar o arquivo", e);
        }
    }

    /**
     * Sanitiza o nome do arquivo removendo caracteres perigosos e path traversal
     */
    private String sanitizarNomeArquivo(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            return "arquivo";
        }

        // Remove path traversal (../) e outros caracteres perigosos
        String seguro = nomeArquivo
                .replaceAll("\\.\\./", "")
                .replaceAll("/", "")
                .replaceAll("\\\\", "")
                .replaceAll("[^a-zA-Z0-9._-]", "_");

        // Garante que não fique vazio após sanitização
        return seguro.isEmpty() ? "arquivo" : seguro;
    }

    /**
     * Detecta o content type baseado na extensão do arquivo
     */
    private String detectarContentType(String nomeArquivo) {
        try {
            String contentType = Files.probeContentType(Path.of(nomeArquivo));
            if (contentType != null) {
                return contentType;
            }
        } catch (IOException ignored) {
            // Se falhar, tenta pela extensão
        }

        // Fallback: detecta pela extensão
        String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1).toLowerCase();
        return switch (extensao) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    /**
     * Gera um nome único para o arquivo mantendo a extensão original
     */
    private String gerarNomeUnico(String nomeArquivo) {
        String extensao = "";
        int pontoIndex = nomeArquivo.lastIndexOf('.');

        if (pontoIndex > 0) {
            extensao = nomeArquivo.substring(pontoIndex);
        }

        return UUID.randomUUID().toString() + extensao;
    }

}
