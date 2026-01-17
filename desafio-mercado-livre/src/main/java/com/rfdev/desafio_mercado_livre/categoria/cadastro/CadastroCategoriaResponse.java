package com.rfdev.desafio_mercado_livre.categoria.cadastro;

import java.util.UUID;

import com.rfdev.desafio_mercado_livre.categoria.Categoria;

public record CadastroCategoriaResponse(
        UUID id,
        String nome,
        String nomeCategoriaMae) {

    public static CadastroCategoriaResponse of(Categoria categoria) {
        String nomeCategoriaMae = categoria.getCategoriaMae() != null
                ? categoria.getCategoriaMae().getNome()
                : null;

        return new CadastroCategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                nomeCategoriaMae);
    }

}
