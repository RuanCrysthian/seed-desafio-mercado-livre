package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import java.io.InputStream;

public interface ObjectStorage {
    String armazenar(String nomeArquivo, InputStream fileContent) throws Exception;
}
