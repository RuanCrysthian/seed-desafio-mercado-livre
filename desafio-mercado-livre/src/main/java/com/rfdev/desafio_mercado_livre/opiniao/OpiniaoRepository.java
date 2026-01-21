package com.rfdev.desafio_mercado_livre.opiniao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OpiniaoRepository extends JpaRepository<Opiniao, UUID> {

}
