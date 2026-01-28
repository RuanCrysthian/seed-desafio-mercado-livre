package com.rfdev.desafio_mercado_livre.compra.processar;


import com.rfdev.desafio_mercado_livre.compra.TipoGatewayPagamento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PagseguroGatewayPagamentoWebhookController {
    private static final String PATH = "/retorno-pagamento-pagseguro";
    private final ProcessadorPagamentoService processadorPagamento;

    public PagseguroGatewayPagamentoWebhookController(ProcessadorPagamentoService processadorPagamento) {
        this.processadorPagamento = processadorPagamento;
    }

    @PostMapping(PATH)
    public ResponseEntity<GatewayPagamentoWebhookResponse> receberNotificacao(@RequestBody @Valid GatewayPagamentoWebhookRequest request) {
        GatewayPagamentoWebhookResponse response = processadorPagamento.processar(
                request.compraOrigemId(),
                request.transacaoGatewayId(),
                request.statusTransacao(),
                TipoGatewayPagamento.PAGSEGURO
        );
        return ResponseEntity.ok().body(response);
    }
}
