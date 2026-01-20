package com.rfdev.desafio_mercado_livre.configuracao.validacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;

@RestControllerAdvice
public class ValidadorErrorHandler {

    private final MessageSource messageSource;

    public ValidadorErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private ResponseEntity<RespostaErro> buildRespostaErro(@NotNull HttpStatus status, @NotNull String error,
            @NotNull List<String> messages) {
        return ResponseEntity.status(status)
                .body(new RespostaErro(LocalDateTime.now(), status.value(), error, messages));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaErro> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> messageSource.getMessage(error, LocaleContextHolder.getLocale()))
                .collect(Collectors.toList());

        return buildRespostaErro(HttpStatus.BAD_REQUEST, "Dados de entrada inválidos", messages);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RespostaErro> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toList());

        return buildRespostaErro(HttpStatus.BAD_REQUEST, "Violação de restrições", messages);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespostaErro> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildRespostaErro(HttpStatus.BAD_REQUEST, "Argumento ilegal", List.of(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RespostaErro> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildRespostaErro(HttpStatus.NOT_FOUND, "Entidade não encontrada", List.of(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespostaErro> handleAccessDeniedException(AccessDeniedException ex) {
        return buildRespostaErro(HttpStatus.FORBIDDEN, "Acesso negado", List.of(ex.getMessage()));
    }

}
