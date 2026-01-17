package com.rfdev.desafio_mercado_livre.categoria;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    boolean existsByNomeAndCategoriaMae(String nome, Categoria categoriaMae);
}
