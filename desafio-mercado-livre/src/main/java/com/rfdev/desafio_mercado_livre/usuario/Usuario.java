package com.rfdev.desafio_mercado_livre.usuario;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;

@Entity
@Table(name = "usuarios")
@Getter
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usuario_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Email
    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @NotBlank
    @Column(name = "senha", nullable = false)
    private String senha;

    @NotNull
    @PastOrPresent
    @Column(name = "criado_em", nullable = false, updatable = false)
    private ZonedDateTime criadoEm = Instant.now().atZone(ZoneId.of("America/Sao_Paulo"));

    @Deprecated
    public Usuario() {
    }

    public Usuario(@NotBlank @Email String login, @NotBlank String senha) {
        this.login = login;
        this.senha = senha;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }
}
