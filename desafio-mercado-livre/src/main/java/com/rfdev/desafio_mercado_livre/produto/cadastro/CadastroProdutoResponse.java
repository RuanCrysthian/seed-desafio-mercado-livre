package com.rfdev.desafio_mercado_livre.produto.cadastro;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.DataUtils;
import com.rfdev.desafio_mercado_livre.produto.Produto;

public record CadastroProdutoResponse(
        UUID id,
        String nome,
        BigDecimal valor,
        BigInteger quantidadeDisponivel,
        List<String> caracteristicas,
        String descricao,
        String categoriaNome,
        Instant criadoEm) {

    public static CadastroProdutoResponse of(Produto produto) {
        return new CadastroProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getValor(),
                produto.getQuantidadeDisponivel(),
                produto.getCaracteristicas(),
                produto.getDescricao(),
                produto.getCategoria().getNome(),
                DataUtils.paraTimeZoneBrasil(produto.getCriadoEm()));
    }
}
