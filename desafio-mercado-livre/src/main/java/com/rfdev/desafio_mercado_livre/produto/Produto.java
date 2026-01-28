package com.rfdev.desafio_mercado_livre.produto;

import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produtos")
@Getter
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "produto_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull
    @Positive
    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @NotNull
    @PositiveOrZero
    @Column(name = "quantidade_disponivel", nullable = false)
    private BigInteger quantidadeDisponivel;

    @NotNull
    @Size(min = 3)
    @ElementCollection
    @CollectionTable(name = "produto_caracteristicas", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "caracteristica")
    private List<String> caracteristicas;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_criador_id", nullable = false)
    private Usuario usuarioCriador;

    @NotNull
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @ElementCollection
    @CollectionTable(name = "produto_imagens", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "imagem")
    private List<String> imagens;

    @Deprecated
    public Produto() {
    }

    public Produto(
            @NotBlank String nome,
            @NotNull @Positive BigDecimal valor,
            @NotNull @PositiveOrZero BigInteger quantidadeDisponivel,
            @NotNull @Size(min = 3) List<String> caracteristicas,
            @NotBlank @Size(max = 1000) String descricao,
            @NotNull Categoria categoria,
            @NotNull Usuario usuarioCriador) {
        this.nome = nome;
        this.valor = valor;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.caracteristicas = caracteristicas;
        this.descricao = descricao;
        this.categoria = categoria;
        this.usuarioCriador = usuarioCriador;
        this.criadoEm = Instant.now();
        this.imagens = new ArrayList<>();
    }

    public void abaterEstoque(@PositiveOrZero BigInteger quantidade) {
        if (!possuiEstoque(quantidade)) {
            throw new IllegalArgumentException("Estoque insuficiente para abater a quantidade solicitada.");
        }
        this.quantidadeDisponivel = this.quantidadeDisponivel.subtract(quantidade);
    }

    public boolean possuiEstoque(@PositiveOrZero BigInteger quantidade) {
        return this.quantidadeDisponivel.compareTo(quantidade) >= 0;
    }

    public boolean pertenceAoUsuario(Usuario usuario) {
        return this.usuarioCriador.getId().equals(usuario.getId());
    }

    public void adicionarImagens(List<String> urlsImagens) {
        this.imagens.addAll(urlsImagens);
    }

}
