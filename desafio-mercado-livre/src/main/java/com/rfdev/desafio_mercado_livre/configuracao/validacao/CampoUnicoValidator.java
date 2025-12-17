package com.rfdev.desafio_mercado_livre.configuracao.validacao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CampoUnicoValidator implements ConstraintValidator<CampoUnico, Object> {

    private Class<?> nomeTabela;
    private String nomeCampo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void initialize(CampoUnico constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.nomeTabela = constraintAnnotation.nomeTabela();
        this.nomeCampo = constraintAnnotation.nomeCampo();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<?> root = criteriaQuery.from(nomeTabela);

            criteriaQuery.select(criteriaBuilder.count(root))
                    .where(criteriaBuilder.equal(root.get(nomeCampo), value));

            Long count = entityManager.createQuery(criteriaQuery).getSingleResult();

            return count == 0;

        } catch (IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
