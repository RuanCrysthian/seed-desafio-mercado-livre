package com.rfdev.desafio_mercado_livre.opiniao.cadastro;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rfdev.desafio_mercado_livre.opiniao.Opiniao;
import com.rfdev.desafio_mercado_livre.opiniao.OpiniaoRepository;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.validation.Valid;

@RestController
public class CadastroOpiniaoController {

    private final OpiniaoRepository opiniaoRepository;
    private final ProdutoRepository produtoRepository;

    public CadastroOpiniaoController(OpiniaoRepository opiniaoRepository, ProdutoRepository produtoRepository) {
        this.opiniaoRepository = opiniaoRepository;
        this.produtoRepository = produtoRepository;
    }

    @PostMapping("/api/opinioes")
    @Transactional
    public ResponseEntity<CadastroOpiniaoResponse> cadastrarOpiniao(
            @RequestBody @Valid CadastroOpiniaoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        Opiniao opiniao = request.toModel(usuarioLogado, produtoRepository);
        opiniaoRepository.save(opiniao);

        return ResponseEntity.ok(CadastroOpiniaoResponse.of(opiniao));
    }
}
