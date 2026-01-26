package com.rfdev.desafio_mercado_livre.opiniao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rfdev.desafio_mercado_livre.produto.Produto;

public interface OpiniaoRepository extends JpaRepository<Opiniao, UUID> {

    List<Opiniao> findByProduto(Produto produto);

}
