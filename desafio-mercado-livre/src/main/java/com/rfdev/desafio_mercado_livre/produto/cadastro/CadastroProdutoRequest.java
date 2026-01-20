package com.rfdev.desafio_mercado_livre.produto.cadastro;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.categoria.CategoriaRepository;
import com.rfdev.desafio_mercado_livre.configuracao.validacao.EntidadeExiste;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CadastroProdutoRequest(
        @NotBlank String nome,
        @NotNull @Positive BigDecimal valor,
        @NotNull @PositiveOrZero BigInteger quantidadeDisponivel,
        @NotNull @Size(min = 3) List<String> caracteristicas,
        @NotBlank @Size(max = 1000) String descricao,
        @NotNull @EntidadeExiste(message = "Categoria não existe", nomeTabela = Categoria.class, nomeCampo = "id") UUID categoriaId) {

    public Produto toModel(
            CategoriaRepository categoriaRepository,
            Usuario usarioCriador) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow(() -> new IllegalArgumentException(
                "Categoria não encontrada"));
        return new Produto(
                nome,
                valor,
                quantidadeDisponivel,
                caracteristicas,
                descricao,
                categoria,
                usarioCriador);
    }
}
