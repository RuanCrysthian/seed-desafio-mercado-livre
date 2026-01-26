package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import org.springframework.stereotype.Component;

import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;

@Component
public class EnviadorEmailConsole implements EnviadorEmail {

    private static final String SEPARADOR_PRINCIPAL = "=".repeat(80);
    private static final String SEPARADOR_SECUNDARIO = "-".repeat(80);
    private static final String QUEBRA_LINHA = System.lineSeparator();

    @Override
    public void enviarEmailNovaPergunta(Pergunta pergunta) {
        String corpoEmail = construirCorpoEmail(pergunta);
        System.out.println(corpoEmail);
    }

    private String construirCorpoEmail(Pergunta pergunta) {
        Produto produto = pergunta.getProduto();
        Usuario vendedor = produto.getUsuarioCriador();
        Usuario autor = pergunta.getUsuarioAutorPergunta();

        StringBuilder email = new StringBuilder();

        email.append(SEPARADOR_PRINCIPAL).append(QUEBRA_LINHA);
        email.append("NOVO EMAIL - NOVA PERGUNTA").append(QUEBRA_LINHA);
        email.append(SEPARADOR_PRINCIPAL).append(QUEBRA_LINHA);

        email.append("Para: ").append(vendedor.getLogin()).append(QUEBRA_LINHA);
        email.append("Assunto: Nova pergunta sobre o produto: ").append(produto.getNome()).append(QUEBRA_LINHA);

        email.append(SEPARADOR_SECUNDARIO).append(QUEBRA_LINHA);
        email.append("Olá,").append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Você recebeu uma nova pergunta sobre o produto '").append(produto.getNome()).append("':")
                .append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Pergunta: ").append(pergunta.getTitulo()).append(QUEBRA_LINHA);
        email.append("De: ").append(autor.getLogin()).append(QUEBRA_LINHA);
        email.append("Data: ").append(pergunta.getCriadaEm()).append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Link para visualização: api/produtos/").append(produto.getId()).append(QUEBRA_LINHA);
        email.append(SEPARADOR_PRINCIPAL);

        return email.toString();
    }

}
