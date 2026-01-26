package com.rfdev.desafio_mercado_livre.pergunta;

import java.time.Instant;
import java.util.UUID;

import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.DataUtils;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "perguntas")
@Getter
public class Pergunta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pergunta_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuarioAutorPergunta;

    @NotNull
    public Instant criadaEm;

    @Enumerated(EnumType.STRING)
    public StatusPergunta status;

    @Deprecated
    public Pergunta() {
    }

    public Pergunta(@NotBlank String titulo, @NotNull Produto produto, @NotNull Usuario usuarioAutorPergunta) {
        this.titulo = titulo;
        this.produto = produto;
        this.usuarioAutorPergunta = usuarioAutorPergunta;
        this.criadaEm = DataUtils.paraTimeZoneBrasil(Instant.now());
        this.status = StatusPergunta.AGUARDANDO_RESPOSTA;
    }

    public void marcarComoRespondida() {
        this.status = StatusPergunta.RESPONDIDA;
    }

}
