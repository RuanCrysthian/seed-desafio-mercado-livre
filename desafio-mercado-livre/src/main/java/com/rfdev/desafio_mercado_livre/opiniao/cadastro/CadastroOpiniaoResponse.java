package com.rfdev.desafio_mercado_livre.opiniao.cadastro;

import java.util.UUID;

import com.rfdev.desafio_mercado_livre.opiniao.Opiniao;

public record CadastroOpiniaoResponse(
        UUID id,
        Integer nota,
        String titulo,
        String descricao,
        String consumidorLogin,
        String produtoNome) {

    public static CadastroOpiniaoResponse of(Opiniao opiniao) {
        return new CadastroOpiniaoResponse(
                opiniao.getId(),
                opiniao.getNota(),
                opiniao.getTitulo(),
                opiniao.getDescricao(),
                opiniao.getConsumidor().getLogin(),
                opiniao.getProduto().getNome());
    }
}
