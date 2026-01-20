package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

@Configuration
public class MinioClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(MinioClientConfig.class);

    @Value("${app.minio.url}")
    private String minioEndpoint;

    @Value("${app.minio.access-key}")
    private String minioAcessKey;

    @Value("${app.minio.secret-key}")
    private String minioSecretKey;

    @Value("${app.minio.bucket-name}")
    private String minioBucketName;

    @Bean
    @ConditionalOnProperty(name = "app.minio.enabled", havingValue = "true", matchIfMissing = true)
    MinioClient minioClient() {
        logger.info("Configurando MinIO client com endpoint: {}", minioEndpoint);
        return MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAcessKey, minioSecretKey)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.minio.enabled", havingValue = "true", matchIfMissing = true)
    String minioBucket(MinioClient minioClient) {
        try {
            logger.info("Verificando existência do bucket: {}", minioBucketName);
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioBucketName)
                            .build());
            if (!exists) {
                logger.info("Bucket não existe. Criando bucket: {}", minioBucketName);
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioBucketName)
                                .build());
                logger.info("Bucket criado com sucesso: {}", minioBucketName);
            } else {
                logger.info("Bucket já existe: {}", minioBucketName);
            }

            return minioBucketName;
        } catch (Exception e) {
            logger.error("Erro ao configurar bucket MinIO. Certifique-se de que o MinIO está rodando.", e);
            throw new RuntimeException("Erro ao configurar bucket MinIO: " + e.getMessage(), e);
        }
    }

    @Bean
    @ConditionalOnProperty(name = "app.minio.enabled", havingValue = "true", matchIfMissing = true)
    ObjectStorage objectStorage(MinioClient minioClient, String minioBucket) {
        logger.info("Configurando ObjectStorage com bucket: {}", minioBucket);
        return new MinioStorage(minioClient, minioBucket);
    }
}
