package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;

public interface EnviadorEmail {

    void enviarEmailNovaPergunta(Pergunta pergunta);

    void enviarEmailDesejoCompra(Compra compra);

}
