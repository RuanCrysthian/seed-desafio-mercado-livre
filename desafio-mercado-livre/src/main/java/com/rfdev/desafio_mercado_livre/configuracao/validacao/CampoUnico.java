package com.rfdev.desafio_mercado_livre.configuracao.validacao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CampoUnicoValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface CampoUnico {

    String message() default "Valor já existe no sistema.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> nomeTabela();

    String nomeCampo();
}
