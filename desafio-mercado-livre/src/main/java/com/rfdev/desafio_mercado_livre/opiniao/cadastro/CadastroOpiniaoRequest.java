package com.rfdev.desafio_mercado_livre.opiniao.cadastro;

import java.util.UUID;

import com.rfdev.desafio_mercado_livre.configuracao.validacao.EntidadeExiste;
import com.rfdev.desafio_mercado_livre.opiniao.Opiniao;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroOpiniaoRequest(
        @NotNull @Min(1) @Max(5) Integer nota,
        @NotBlank String titulo,
        @NotBlank @Size(max = 500) String descricao,
        @NotNull @EntidadeExiste(message = "Produto não encontrado.", nomeTabela = Produto.class, nomeCampo = "id") UUID produtoId) {

    public Opiniao toModel(Usuario consumidor, ProdutoRepository produtoRepository) {
        Produto produto = produtoRepository.findById(produtoId()).orElseThrow(
                () -> new IllegalArgumentException("Produto não encontrado."));
        return new Opiniao(nota, titulo, descricao, consumidor, produto);
    }
}
