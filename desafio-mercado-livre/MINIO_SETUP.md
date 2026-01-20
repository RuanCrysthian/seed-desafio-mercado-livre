# Configuração do MinIO para Upload de Imagens

## 🚀 Como Iniciar o MinIO

### 1. Usando Docker Compose (Recomendado)

```bash
# Inicia todos os serviços (PostgreSQL + MinIO)
docker-compose up -d

# Apenas o MinIO
docker-compose up -d minio

# Parar os serviços
docker-compose down
```

### 2. Acessar o Console do MinIO

- **URL**: http://localhost:9001
- **Usuário**: ROOTNAME
- **Senha**: CHANGEME123

### 3. Verificar o Bucket

Após iniciar, o bucket `produtos` será criado automaticamente pela aplicação.

## 🔧 Configurações

### Development (Local sem Docker)

Se você NÃO quiser usar MinIO em desenvolvimento, adicione no `application.properties`:

```properties
app.minio.enabled=false
```

### Production (Com Docker)

As configurações já estão no `application-prod.properties`:

```properties
app.minio.url=http://localhost:9000
app.minio.bucket-name=produtos
app.minio.access-key=ROOTNAME
app.minio.secret-key=CHANGEME123
```

## 📝 Testando o Upload

### Usando cURL:

```bash
# 1. Fazer login e obter token
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user@email.com","senha":"SenhaForte123!"}'

# 2. Upload de imagens (substitua {token} e {produto-id})
curl -X POST http://localhost:8080/api/produtos/{produto-id}/imagens \
  -H "Authorization: Bearer {token}" \
  -F "imagens=@imagem1.jpg" \
  -F "imagens=@imagem2.png"
```

### Exemplo de Response:

```json
{
  "produtoId": "550e8400-e29b-41d4-a716-446655440000",
  "imagens": [
    {
      "url": "http://localhost:9000/produtos/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
      "nomeArquivo": "a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
      "tamanho": 245678
    }
  ]
}
```

## 🔒 Limitações

- **Tipos aceitos**: JPEG, PNG, GIF, WebP
- **Tamanho máximo**: 5MB por imagem
- **Quantidade máxima**: 10 imagens por upload
- **Permissão**: Apenas o dono do produto pode fazer upload

## 🐛 Troubleshooting

### Erro: "UnknownHostException: minio"

Certifique-se de que o MinIO está rodando:

```bash
docker ps | grep minio
```

Se não estiver, inicie:

```bash
docker-compose up -d minio
```

### Erro: "Connection refused"

Verifique se a porta 9000 está disponível:

```bash
lsof -i :9000
```

### Desabilitar MinIO temporariamente

Adicione no `application.properties`:

```properties
app.minio.enabled=false
```

## 📦 Estrutura no Banco

As URLs das imagens são salvas na tabela `produto_imagens`:

| produto_id | imagem |
|------------|--------|
| uuid-123   | http://localhost:9000/produtos/file1.jpg |
| uuid-123   | http://localhost:9000/produtos/file2.png |
