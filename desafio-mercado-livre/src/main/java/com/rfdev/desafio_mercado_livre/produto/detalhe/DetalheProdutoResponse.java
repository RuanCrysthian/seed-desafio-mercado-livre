package com.rfdev.desafio_mercado_livre.produto.detalhe;

import com.rfdev.desafio_mercado_livre.produto.Produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record DetalheProdutoResponse(
        String nome,
        String descricao,
        BigDecimal preco,
        List<String> caracteristicas,
        List<String> linksImagens,
        Double mediaNotas,
        Integer totalNotas,
        List<OpiniaoResponse> opinioes,
        List<PerguntaResponse> perguntas
) {

    public record OpiniaoResponse(
            Integer nota,
            String titulo,
            String descricao
    ) {
    }

    public record PerguntaResponse(
            String titulo
    ) {
    }

    public static DetalheProdutoResponse of(
            Produto produto,
            List<OpiniaoResponse> opinioes,
            List<PerguntaResponse> perguntas
    ) {
        Double mediaNotas = calcularMediaNotas(opinioes);
        Integer totalNotas = opinioes.size();

        return new DetalheProdutoResponse(
                produto.getNome(),
                produto.getDescricao(),
                produto.getValor(),
                produto.getCaracteristicas(),
                produto.getImagens(),
                mediaNotas,
                totalNotas,
                opinioes,
                perguntas
        );
    }

    private static Double calcularMediaNotas(List<OpiniaoResponse> opinioes) {
        if (opinioes == null || opinioes.isEmpty()) {
            return 0.0;
        }

        double soma = opinioes.stream()
                .mapToInt(OpiniaoResponse::nota)
                .sum();

        return BigDecimal.valueOf(soma / opinioes.size())
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
