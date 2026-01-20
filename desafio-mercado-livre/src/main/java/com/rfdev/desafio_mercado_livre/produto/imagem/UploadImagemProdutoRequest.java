package com.rfdev.desafio_mercado_livre.produto.imagem;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UploadImagemProdutoRequest(
        @NotEmpty(message = "É necessário enviar pelo menos uma imagem") @Size(max = 10, message = "Número máximo de imagens é 10") List<MultipartFile> imagens) {
}
