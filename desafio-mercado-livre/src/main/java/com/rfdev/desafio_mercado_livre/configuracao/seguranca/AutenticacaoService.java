package com.rfdev.desafio_mercado_livre.configuracao.seguranca;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

@Component
public class AutenticacaoService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    public LoginResponse gerarToken(Usuario usuario) {
        // Lógica para gerar o token JWT
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            Instant expiresAt = Instant.now().plusSeconds(60 * 30);

            String token = JWT.create()
                    .withIssuer(jwtIssuer)
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(expiresAt)
                    .sign(algorithm);

            return new LoginResponse(token, expiresAt);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT de acesso!");
        }
    }

    public String verificarToken(String token) {
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtIssuer)
                    .build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Erro ao verificar token JWT de acesso!");
        }
    }
}
