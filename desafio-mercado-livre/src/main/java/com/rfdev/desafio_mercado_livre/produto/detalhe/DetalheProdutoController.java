package com.rfdev.desafio_mercado_livre.produto.detalhe;

import com.rfdev.desafio_mercado_livre.opiniao.Opiniao;
import com.rfdev.desafio_mercado_livre.opiniao.OpiniaoRepository;
import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.pergunta.PerguntaRepository;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.produto.detalhe.DetalheProdutoResponse.OpiniaoResponse;
import com.rfdev.desafio_mercado_livre.produto.detalhe.DetalheProdutoResponse.PerguntaResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class DetalheProdutoController {

    private final ProdutoRepository produtoRepository;
    private final OpiniaoRepository opiniaoRepository;
    private final PerguntaRepository perguntaRepository;

    public DetalheProdutoController(
            ProdutoRepository produtoRepository,
            OpiniaoRepository opiniaoRepository,
            PerguntaRepository perguntaRepository) {
        this.produtoRepository = produtoRepository;
        this.opiniaoRepository = opiniaoRepository;
        this.perguntaRepository = perguntaRepository;
    }

    @GetMapping("/api/produtos/{produtoId}")
    public ResponseEntity<DetalheProdutoResponse> detalharProduto(@PathVariable UUID produtoId) {

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        List<Opiniao> opinioes = opiniaoRepository.findByProduto(produto);
        List<OpiniaoResponse> opinioesResponse = opinioes.stream()
                .map(opiniao -> new OpiniaoResponse(
                        opiniao.getNota(),
                        opiniao.getTitulo(),
                        opiniao.getDescricao()
                ))
                .toList();

        List<Pergunta> perguntas = perguntaRepository.findByProduto(produto);
        List<PerguntaResponse> perguntasResponse = perguntas.stream()
                .map(pergunta -> new PerguntaResponse(pergunta.getTitulo()))
                .toList();

        DetalheProdutoResponse response = DetalheProdutoResponse.of(produto, opinioesResponse, perguntasResponse);

        return ResponseEntity.ok(response);
    }
}
