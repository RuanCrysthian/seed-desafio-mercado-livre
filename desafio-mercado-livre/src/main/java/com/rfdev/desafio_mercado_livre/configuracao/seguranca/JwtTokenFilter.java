package com.rfdev.desafio_mercado_livre.configuracao.seguranca;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import com.rfdev.desafio_mercado_livre.usuario.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final AutenticacaoService autenticacaoService;
    private final UsuarioRepository usuarioRepository;

    public JwtTokenFilter(AutenticacaoService autenticacaoService, UsuarioRepository usuarioRepository) {
        this.autenticacaoService = autenticacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = recuperarTokenRequisicao(request);

        if (token != null) {
            String login = autenticacaoService.verificarToken(token);
            Usuario usuario = usuarioRepository.findByLogin(login).orElseThrow();

            Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null,
                    usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarTokenRequisicao(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
