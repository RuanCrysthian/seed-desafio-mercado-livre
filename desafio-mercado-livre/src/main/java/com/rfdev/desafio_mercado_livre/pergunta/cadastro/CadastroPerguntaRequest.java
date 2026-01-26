package com.rfdev.desafio_mercado_livre.pergunta.cadastro;

import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.validation.constraints.NotBlank;

public record CadastroPerguntaRequest(
        @NotBlank String titulo) {

    public Pergunta toModel(Produto produto, Usuario usuario) {
        return new Pergunta(titulo, produto, usuario);
    }

}
