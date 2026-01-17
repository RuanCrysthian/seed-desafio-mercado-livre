package com.rfdev.desafio_mercado_livre.configuracao.validacao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;

public class EntidadeExisteValidator implements ConstraintValidator<EntidadeExiste, Object> {

    private Class<?> nomeTabela;
    private String nomeCampo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void initialize(EntidadeExiste constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.nomeTabela = constraintAnnotation.nomeTabela();
        this.nomeCampo = constraintAnnotation.nomeCampo();
    }

    @Override
    public boolean isValid(Object value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String jpql = String.format(
                "SELECT COUNT(e) FROM %s e WHERE e.%s = :value",
                nomeTabela.getSimpleName(), nomeCampo);

        var query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("value", value);
        Long count = (Long) query.getSingleResult();

        return count > 0;
    }
}
