package com.rfdev.desafio_mercado_livre.categoria.cadastro;

import java.util.UUID;

import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.categoria.CategoriaRepository;
import com.rfdev.desafio_mercado_livre.configuracao.validacao.CampoUnico;
import com.rfdev.desafio_mercado_livre.configuracao.validacao.EntidadeExiste;

import jakarta.validation.constraints.NotBlank;

public record CadastroCategoriaRequest(
        @NotBlank(message = "Nome é obrigatório.") @CampoUnico(message = "Nome já cadastrado no sistema.", nomeTabela = Categoria.class, nomeCampo = "nome") String nome,
        @EntidadeExiste(message = "Categoria mãe não encontrada.", nomeTabela = Categoria.class, nomeCampo = "id") UUID categoriaMaeId) {

    public Categoria toModel(CategoriaRepository categoriaRepository) {
        if (this.categoriaMaeId == null) {
            return new Categoria(this.nome, null);
        }

        Categoria categoriaMae = categoriaRepository.findById(this.categoriaMaeId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria mãe não encontrada."));
        return new Categoria(this.nome, categoriaMae);
    }
}
