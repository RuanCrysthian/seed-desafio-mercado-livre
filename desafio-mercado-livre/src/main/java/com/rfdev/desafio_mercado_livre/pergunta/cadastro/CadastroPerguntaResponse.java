package com.rfdev.desafio_mercado_livre.pergunta.cadastro;

import java.time.Instant;
import java.util.UUID;

import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.pergunta.StatusPergunta;

public record CadastroPerguntaResponse(
        UUID id,
        String titulo,
        String nomeProduto,
        String nomeUsuarioAutorPergunta,
        Instant criadaEm,
        StatusPergunta status) {

    public static CadastroPerguntaResponse from(Pergunta pergunta) {
        return new CadastroPerguntaResponse(
                pergunta.getId(),
                pergunta.getTitulo(),
                pergunta.getProduto().getNome(),
                pergunta.getUsuarioAutorPergunta().getLogin(),
                pergunta.getCriadaEm(),
                pergunta.getStatus());
    }

}
