# Sistema de Nota Fiscal - Microserviço Fake

## 📋 Descrição

Este módulo simula um microserviço externo de Nota Fiscal. Ele foi desenvolvido para receber informações de compras
concluídas com sucesso e simular o processamento de emissão de nota fiscal.

## 🏗️ Estrutura

### 1. **CadastroNotaFiscalController**

Controller fake que expõe o endpoint `/api/notas-fiscais` para simular o microserviço de nota fiscal.

**Endpoint:**

```
POST /api/notas-fiscais
```

**Request Body:**

```json
{
  "compraId": "uuid-da-compra",
  "compradorId": "uuid-do-comprador"
}
```

**Response:**

```json
{
  "notaFiscalId": "uuid-gerado-automaticamente",
  "compraId": "uuid-da-compra",
  "compradorId": "uuid-do-comprador",
  "status": "PROCESSADA",
  "mensagem": "Nota fiscal gerada com sucesso"
}
```

### 2. **NotaFiscalService**

Serviço que realiza a comunicação com o endpoint fake. Este serviço deve ser chamado quando uma compra for concluída com
sucesso.

**Exemplo de uso:**

```java

@Service
public class ProcessadorCompraService {

    private final NotaFiscalService notaFiscalService;

    public void processarCompraConcluida(Compra compra) {
        // ... lógica de processamento da compra

        if (compra.getStatus() == CompraStatus.PAGA) {
            // Envia para o sistema de nota fiscal
            notaFiscalService.notificarCompraConcluida(compra);
        }
    }
}
```

### 3. **DTOs**

- `CadastroNotaFiscalRequest`: Representa os dados necessários para gerar uma nota fiscal
    - `compraId`: UUID da compra
    - `compradorId`: UUID do usuário que realizou a compra

- `CadastroNotaFiscalResponse`: Representa a resposta do sistema de nota fiscal
    - `notaFiscalId`: UUID gerado para a nota fiscal
    - `compraId`: UUID da compra relacionada
    - `compradorId`: UUID do comprador
    - `status`: Status do processamento
    - `mensagem`: Mensagem descritiva do resultado

## 🚀 Como Testar

### Usando cURL:

```bash
curl -X POST http://localhost:8080/api/notas-fiscais \
  -H "Content-Type: application/json" \
  -d '{
    "compraId": "123e4567-e89b-12d3-a456-426614174000",
    "compradorId": "987fcdeb-51a2-43d7-9012-345678901234"
  }'
```

### Usando Postman:

1. Método: `POST`
2. URL: `http://localhost:8080/api/notas-fiscais`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):

```json
{
  "compraId": "123e4567-e89b-12d3-a456-426614174000",
  "compradorId": "987fcdeb-51a2-43d7-9012-345678901234"
}
```

## 📝 Logs

O sistema gera logs detalhados para facilitar o rastreamento:

```
🧾 [SISTEMA DE NOTA FISCAL] Recebendo requisição para gerar nota fiscal
   - ID da Compra: 123e4567-e89b-12d3-a456-426614174000
   - ID do Comprador: 987fcdeb-51a2-43d7-9012-345678901234
✅ [SISTEMA DE NOTA FISCAL] Nota fiscal gerada com sucesso!
   - ID da Nota Fiscal: abc12345-6789-0def-1234-567890abcdef
```

## 🔧 Configuração

O `RestTemplate` está configurado em `RestTemplateConfig` com:

- **Connect Timeout**: 5 segundos
- **Read Timeout**: 5 segundos

## 📌 Observações

- Este é um **mock/fake** de microserviço para fins de desenvolvimento e testes
- Em produção, este endpoint seria substituído por um serviço real de nota fiscal
- A URL do serviço pode ser configurada via `application.properties`
- O serviço não persiste dados - apenas simula o processamento

## 🔐 Segurança

⚠️ **Importante:** Este endpoint está atualmente sem autenticação. Se necessário, adicione-o às configurações de
segurança em `SegurancaConfig.java`:

```java

@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            // ... configurações existentes
            .authorizeHttpRequests(req -> {
                req.requestMatchers("/api/notas-fiscais").permitAll(); // ou authenticated()
                // ... outras regras
            })
            .build();
}
```

## 🎯 Próximos Passos

Para integrar este sistema ao fluxo de compras:

1. Adicione um método `marcarComoPaga()` na classe `Compra`
2. Crie um service/controller que processe o callback do gateway de pagamento
3. Ao receber confirmação de pagamento, chame `notaFiscalService.notificarCompraConcluida(compra)`
4. Opcionalmente, implemente retry logic e tratamento de falhas

## 📚 Referências

- [Spring RestTemplate Documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)
- [Spring Boot Web Services](https://spring.io/guides/gs/producing-web-service/)
