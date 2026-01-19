package com.rfdev.desafio_mercado_livre.configuracao.seguranca;

import java.time.Instant;

public record LoginResponse(String token, Instant expiracao) {

}
