package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DataUtils {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    private DataUtils() {
    }

    public static Instant agora() {
        return ZonedDateTime.now(BRAZIL_ZONE).toInstant();
    }

    public static Instant paraTimeZoneBrasil(Instant instant) {
        return instant.atZone(BRAZIL_ZONE).toInstant();
    }
}
