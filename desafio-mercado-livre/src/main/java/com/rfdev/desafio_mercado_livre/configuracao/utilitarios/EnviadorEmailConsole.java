package com.rfdev.desafio_mercado_livre.configuracao.utilitarios;

import com.rfdev.desafio_mercado_livre.compra.Compra;
import com.rfdev.desafio_mercado_livre.pergunta.Pergunta;
import com.rfdev.desafio_mercado_livre.produto.Produto;
import com.rfdev.desafio_mercado_livre.usuario.Usuario;
import org.springframework.stereotype.Component;

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

    @Override
    public void enviarEmailDesejoCompra(Compra compra) {
        String corpoEmail = construirCorpoEmailCompra(compra);
        System.out.println(corpoEmail);
    }

    @Override
    public void enviarEmailPagamentoFalhou(Compra compra) {
        String corpoEmail = contruirCorpoEmailPagamentoFalhou(compra);
        System.out.println(corpoEmail);
    }

    @Override
    public void enviarEmailCompraConfirmada(Compra compra) {
        String corpoEmail = contruirCorpoEmailCompraConfirmada(compra);
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

    private String construirCorpoEmailCompra(Compra compra) {
        Produto produto = compra.getProduto();
        Usuario vendedor = produto.getUsuarioCriador();
        Usuario comprador = compra.getComprador();

        StringBuilder email = new StringBuilder();

        email.append(SEPARADOR_PRINCIPAL).append(QUEBRA_LINHA);
        email.append("NOVO EMAIL - INTERESSE DE COMPRA").append(QUEBRA_LINHA);
        email.append(SEPARADOR_PRINCIPAL).append(QUEBRA_LINHA);

        email.append("Para: ").append(vendedor.getLogin()).append(QUEBRA_LINHA);
        email.append("Assunto: Interesse de compra no produto: ").append(produto.getNome()).append(QUEBRA_LINHA);

        email.append(SEPARADOR_SECUNDARIO).append(QUEBRA_LINHA);
        email.append("Olá,").append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Um comprador demonstrou interesse em adquirir o produto '").append(produto.getNome()).append("':")
                .append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Comprador: ").append(comprador.getLogin()).append(QUEBRA_LINHA);
        email.append("Quantidade: ").append(compra.getQuantidade()).append(QUEBRA_LINHA);
        email.append("Gateway de Pagamento: ").append(compra.getTipoGatewayPagamento()).append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Link para visualização: api/produtos/").append(produto.getId()).append(QUEBRA_LINHA);
        email.append(SEPARADOR_PRINCIPAL);

        return email.toString();
    }

    private String contruirCorpoEmailPagamentoFalhou(Compra compra) {
        Produto produto = compra.getProduto();
        Usuario vendedor = produto.getUsuarioCriador();
        Usuario comprador = compra.getComprador();

        StringBuilder email = new StringBuilder();

        email.append(SEPARADOR_PRINCIPAL).append(QUEBRA_LINHA);
        email.append("NOVO EMAIL - PAGAMENTO FALHOU").append(QUEBRA_LINHA);
        email.append(SEPARADOR_PRINCIPAL).append(QUEBRA_LINHA);

        email.append("Para: ").append(vendedor.getLogin()).append(QUEBRA_LINHA);
        email.append("Assunto: Falha no pagamento da compra do produto: ").append(produto.getNome()).append(QUEBRA_LINHA);

        email.append(SEPARADOR_SECUNDARIO).append(QUEBRA_LINHA);
        email.append("Olá,").append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Houve uma falha no pagamento da compra do produto '").append(produto.getNome()).append("':")
                .append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Comprador: ").append(comprador.getLogin()).append(QUEBRA_LINHA);
        email.append("Quantidade: ").append(compra.getQuantidade()).append(QUEBRA_LINHA);
        email.append("Gateway de Pagamento: ").append(compra.getTipoGatewayPagamento()).append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Link para visualização: api/produtos/").append(produto.getId()).append(QUEBRA_LINHA);
        email.append(SEPARADOR_PRINCIPAL);

        return email.toString();
    }

    private String contruirCorpoEmailCompraConfirmada(Compra compra) {
        Produto produto = compra.getProduto();
        Usuario vendedor = produto.getUsuarioCriador();
        Usuario comprador = compra.getComprador();

        StringBuilder email = new StringBuilder();

        email.append(SEPARADOR_PRINCIPAL).append(QUEBRA_LINHA);
        email.append("NOVO EMAIL - COMPRA CONFIRMADA").append(QUEBRA_LINHA);
        email.append(SEPARADOR_PRINCIPAL).append(QUEBRA_LINHA);

        email.append("Para: ").append(vendedor.getLogin()).append(QUEBRA_LINHA);
        email.append("Assunto: Compra confirmada do produto: ").append(produto.getNome()).append(QUEBRA_LINHA);

        email.append(SEPARADOR_SECUNDARIO).append(QUEBRA_LINHA);
        email.append("Olá,").append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("A compra do produto '").append(produto.getNome()).append("' foi confirmada com sucesso:")
                .append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Comprador: ").append(comprador.getLogin()).append(QUEBRA_LINHA);
        email.append("Quantidade: ").append(compra.getQuantidade()).append(QUEBRA_LINHA);
        email.append("Gateway de Pagamento: ").append(compra.getTipoGatewayPagamento()).append(QUEBRA_LINHA);
        email.append(QUEBRA_LINHA);

        email.append("Link para visualização: api/produtos/").append(produto.getId()).append(QUEBRA_LINHA);
        email.append(SEPARADOR_PRINCIPAL);

        return email.toString();
    }

}
