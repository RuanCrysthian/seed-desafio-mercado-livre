package com.rfdev.desafio_mercado_livre.categoria.cadastro;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rfdev.desafio_mercado_livre.categoria.Categoria;
import com.rfdev.desafio_mercado_livre.categoria.CategoriaRepository;

import jakarta.validation.Valid;

@RestController
public class CadastroCategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CadastroCategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping("/api/categorias")
    @Transactional
    public ResponseEntity<CadastroCategoriaResponse> cadastrar(@RequestBody @Valid CadastroCategoriaRequest request) {
        Categoria categoria = request.toModel(categoriaRepository);
        categoriaRepository.save(categoria);

        return ResponseEntity.ok(CadastroCategoriaResponse.of(categoria));
    }
}
