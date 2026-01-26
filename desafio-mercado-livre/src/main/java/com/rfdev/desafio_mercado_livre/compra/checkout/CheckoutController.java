package com.rfdev.desafio_mercado_livre.compra.checkout;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.compra.CompraRepository;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.EnviadorEmail;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.GatewayPagamento;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.produto.ProdutoRepository;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckoutController {

    private final CompraRepository compraRepository;
    private final ProdutoRepository produtoRepository;
    private final EnviadorEmail enviadorEmail;
    private final GatewayPagamento gatewayPagamento;

    public CheckoutController(
            CompraRepository compraRepository,
            ProdutoRepository produtoRepository,
            EnviadorEmail enviadorEmail,
            GatewayPagamento gatewayPagamento) {
        this.compraRepository = compraRepository;
        this.produtoRepository = produtoRepository;
        this.enviadorEmail = enviadorEmail;
        this.gatewayPagamento = gatewayPagamento;
    }

    @PostMapping("/api/checkout")
    @Transactional
    public ResponseEntity<String> finalizarCompra(
            @RequestBody @Valid CheckoutRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
        if (!produto.possuiEstoque(request.quantidade())) {
            throw new IllegalArgumentException("Estoque insuficiente para abater a quantidade solicitada.");
        }
        Compra compra = request.toModel(
                produto,
                usuarioLogado
        );
        produto.abaterEstoque(request.quantidade());
        produtoRepository.save(produto);
        compraRepository.save(compra);
        enviadorEmail.enviarEmailDesejoCompra(compra);
        String urlGatewayPagamento = gatewayPagamento.processarCompra(compra.getId());
        return ResponseEntity.status(HttpStatus.FOUND).body(urlGatewayPagamento);
    }
}
