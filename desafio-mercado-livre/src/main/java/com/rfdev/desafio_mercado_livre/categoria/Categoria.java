package com.rfdev.desafio_mercado_livre.categoria;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String nome;

    @ManyToOne
    private Categoria categoriaMae;

    @Deprecated
    public Categoria() {
    }

    public Categoria(@NotBlank String nome, Categoria categoriaMae) {
        this.nome = nome;
        this.categoriaMae = categoriaMae;
    }
}
