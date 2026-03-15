package com.rfdev.desafio_mercado_livre.produto.imagem;

import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.ObjectStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;

@Service
@ConditionalOnProperty(name = "app.minio.enabled", havingValue = "true", matchIfMissing = true)
public class AsyncStorageService {

    private final ObjectStorage objectStorage;

    public AsyncStorageService(ObjectStorage objectStorage) {
        this.objectStorage = objectStorage;
    }

    @Async
    public CompletableFuture<String> armazenarAsync(String nomeArquivo, byte[] bytes) throws Exception {
        String nomeUnico = objectStorage.armazenar(nomeArquivo, new ByteArrayInputStream(bytes));
        return CompletableFuture.completedFuture(nomeUnico);
    }
}
