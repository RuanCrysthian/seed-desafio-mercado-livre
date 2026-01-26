package com.rfdev.desafio_mercado_livre.pergunta.cadastro;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.EnviadorEmail;
import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.pergunta.PerguntaRepository;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.validation.Valid;

@RestController
public class CadastroPerguntaController {

    private final PerguntaRepository perguntaRepository;
    private final ProdutoRepository produtoRepository;
    private final EnviadorEmail enviadorEmail;

    public CadastroPerguntaController(
            PerguntaRepository perguntaRepository,
            ProdutoRepository produtoRepository,
            EnviadorEmail enviadorEmail) {
        this.perguntaRepository = perguntaRepository;
        this.produtoRepository = produtoRepository;
        this.enviadorEmail = enviadorEmail;
    }

    @PostMapping("/api/produtos/{produtoId}/perguntas")
    @Transactional
    public ResponseEntity<List<CadastroPerguntaResponse>> cadastrar(
            @PathVariable UUID produtoId,
            @RequestBody @Valid CadastroPerguntaRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        Pergunta novaPergunta = request.toModel(produto, usuarioLogado);
        perguntaRepository.save(novaPergunta);

        // TODO: Fazer de forma assíncrona usando filas/mensageria
        // TODO: Implementar serviço de email real - POSTHOG
        enviadorEmail.enviarEmailNovaPergunta(novaPergunta);

        List<Pergunta> perguntas = perguntaRepository.findByProduto(novaPergunta.getProduto());
        List<CadastroPerguntaResponse> response = perguntas.stream()
                .map(CadastroPerguntaResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

}
