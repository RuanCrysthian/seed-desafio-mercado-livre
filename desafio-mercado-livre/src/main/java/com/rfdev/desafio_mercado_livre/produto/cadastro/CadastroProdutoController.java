package com.rfdev.desafio_mercado_livre.produto.cadastro;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rfdev.desafio_mercado_livre.categoria.CategoriaRepository;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.validation.Valid;

@RestController
public class CadastroProdutoController {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public CadastroProdutoController(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping("/api/produtos")
    @Transactional
    public ResponseEntity<CadastroProdutoResponse> cadastrarProduto(
            @RequestBody @Valid CadastroProdutoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        Produto produto = request.toModel(categoriaRepository, usuarioLogado);
        produtoRepository.save(produto);

        return ResponseEntity.ok(CadastroProdutoResponse.of(produto));
    }
}
