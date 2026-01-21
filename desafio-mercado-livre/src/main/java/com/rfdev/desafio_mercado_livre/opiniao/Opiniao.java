package com.rfdev.desafio_mercado_livre.opiniao;

import java.util.UUID;

import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Entity
@Table(name = "opinioes")
@Getter
public class Opiniao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "opiniao_id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "nota", nullable = false)
    private Integer nota;

    @NotBlank
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @NotBlank
    @Size(max = 500)
    @Column(name = "descricao", nullable = false, length = 500)
    private String descricao;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "consumidor_id", nullable = false)
    private Usuario consumidor;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Deprecated
    public Opiniao() {
    }

    public Opiniao(
            Integer nota,
            String titulo,
            String descricao,
            Usuario consumidor,
            Produto produto) {
        this.nota = nota;
        this.titulo = titulo;
        this.descricao = descricao;
        this.consumidor = consumidor;
        this.produto = produto;
    }
}
