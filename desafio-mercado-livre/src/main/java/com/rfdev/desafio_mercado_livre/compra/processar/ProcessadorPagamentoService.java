package com.rfdev.desafio_mercado_livre.compra.processar;

import com.rfdev.desafio_mercado_livre.compra.*;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.EnviadorEmail;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.TradutorStatusTransacao;
import com.rfdev.desafio_mercado_livre.configuracao.utilitarios.TradutorStatusTransacaoFactory;
import com.rfdev.desafio_mercado_livre.notafiscal.NotaFiscalService;
import com.rfdev.desafio_mercado_livre.vendedores.RankingVendedoresService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProcessadorPagamentoService {

    private final TransacaoPagamentoRepository transacaoRepository;
    private final CompraRepository compraRepository;
    private final NotaFiscalService notaFiscalService;
    private final EnviadorEmail enviadorEmail;
    private final RankingVendedoresService rankingService;

    public ProcessadorPagamentoService(
            TransacaoPagamentoRepository transacaoRepository,
            CompraRepository compraRepository,
            NotaFiscalService notaFiscalService,
            EnviadorEmail enviadorEmail,
            RankingVendedoresService rankingService) {
        this.transacaoRepository = transacaoRepository;
        this.compraRepository = compraRepository;
        this.notaFiscalService = notaFiscalService;
        this.enviadorEmail = enviadorEmail;
        this.rankingService = rankingService;
    }

    @Transactional
    public GatewayPagamentoWebhookResponse processar(UUID compraId, UUID transacaoGatewayId, Object statusGateway, TipoGatewayPagamento gateway) {

        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada."));

        validarTransacaoNaoPodeSerProcessada(compra, transacaoGatewayId, gateway);

        PagamentoStatus status = traduzirStatus(statusGateway, gateway);

        TransacaoPagamento transacaoPagamento = registrarTransacao(compra, transacaoGatewayId, status, gateway);

        if (status == PagamentoStatus.FALHA) {
            processarFalha(compra);
        } else {
            processarSucesso(compra);
        }

        return GatewayPagamentoWebhookResponse.of(transacaoPagamento);
    }

    private void validarTransacaoNaoPodeSerProcessada(Compra compra, UUID transacaoId, TipoGatewayPagamento gateway) {
        if (compra.compraFoiConcluidaComSucesso()) {
            throw new IllegalArgumentException("Compra já foi concluída com sucesso");
        }

        if (transacaoRepository.existsByTransacaoGatewayId(transacaoId)) {
            throw new IllegalArgumentException("Transação já foi processada");
        }

        if (transacaoRepository.existsByCompraOrigemAndTipoGatewayPagamento(compra, gateway)) {
            throw new IllegalArgumentException("Já existe transação para esta compra neste gateway");
        }
    }

    private PagamentoStatus traduzirStatus(Object statusGateway, TipoGatewayPagamento gateway) {
        TradutorStatusTransacao tradutor = TradutorStatusTransacaoFactory.criarTradutor(gateway);
        return tradutor.traduzirStatus(statusGateway);
    }

    private TransacaoPagamento registrarTransacao(Compra compra, UUID transacaoId, PagamentoStatus status, TipoGatewayPagamento gateway) {
        TransacaoPagamento transacao = new TransacaoPagamento(compra, transacaoId, status, gateway);
        transacaoRepository.save(transacao);
        return transacao;
    }

    private void processarFalha(Compra compra) {
        enviadorEmail.enviarEmailPagamentoFalhou(compra);
    }

    private void processarSucesso(Compra compra) {
        compra.concluirCompra();
        compraRepository.save(compra);

        notaFiscalService.notificarCompraConcluida(compra);
        rankingService.notificarVenda(compra);
        enviadorEmail.enviarEmailCompraConfirmada(compra);
    }

}
