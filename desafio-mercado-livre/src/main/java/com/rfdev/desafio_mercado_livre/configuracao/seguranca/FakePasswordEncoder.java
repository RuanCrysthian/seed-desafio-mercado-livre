package com.rfdev.desafio_mercado_livre.configuracao.seguranca;

import org.springframework.stereotype.Component;

@Component
public class FakePasswordEncoder implements PasswordEncoder {
    public String encode(String senha) {
        return senha; // Não faz nenhuma codificação real
    }
}
