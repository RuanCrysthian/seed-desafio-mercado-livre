package com.rfdev.desafio_mercado_livre.pergunta.cadastro;

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rfdev.desafio_mercado_livre.configuracao.mensageria.RabbitMQConfig;
import com.rfdev.desafio_mercado_livre.configuracao.mensageria.eventos.EventoPerguntaCriada;
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
    private final RabbitTemplate rabbitTemplate;

    public CadastroPerguntaController(
            PerguntaRepository perguntaRepository,
            ProdutoRepository produtoRepository,
            RabbitTemplate rabbitTemplate) {
        this.perguntaRepository = perguntaRepository;
        this.produtoRepository = produtoRepository;
        this.rabbitTemplate = rabbitTemplate;
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

        UUID perguntaId = novaPergunta.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE,
                        RabbitMQConfig.RK_PERGUNTA_CRIADA,
                        new EventoPerguntaCriada(perguntaId));
            }
        });

        List<Pergunta> perguntas = perguntaRepository.findByProduto(novaPergunta.getProduto());
        List<CadastroPerguntaResponse> response = perguntas.stream()
                .map(CadastroPerguntaResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

}
