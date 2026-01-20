package com.rfdev.desafio_mercado_livre.produto.imagem;

import java.util.List;
import java.util.UUID;

public record UploadImagemProdutoResponse(
        UUID produtoId,
        List<ImagemUploadada> imagens) {

    public record ImagemUploadada(
            String url,
            String nomeArquivo) {
    }

    public static UploadImagemProdutoResponse of(UUID produtoId, List<ImagemUploadada> imagens) {
        return new UploadImagemProdutoResponse(produtoId, imagens);
    }
}
