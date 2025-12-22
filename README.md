# Product Service

Serviço de gerenciamento de produtos desenvolvido com Spring Boot 4, seguindo princípios de Clean Architecture e arquitetura hexagonal.

## Stack Tecnológica

- **Java 21**
- **Spring Boot 4.0.1**
- **PostgreSQL**
- **Redis** (cache distribuído)
- **Flyway** (migrações de banco de dados)
- **Docker & Docker Compose**

## Arquitetura

O projeto segue os princípios de Clean Architecture com separação clara de responsabilidades:

```
src/main/java/com/techsolution/product_service/
├── domain/              # Camada de domínio (independente de framework)
│   ├── Product.java
│   ├── ProductRepository.java
│   └── exception/
├── application/         # Casos de uso (orquestração)
│   └── usecase/
├── infrastructure/      # Implementações técnicas
│   └── persistence/
└── interfaces/          # Controllers e DTOs
    ├── controller/
    ├── dto/
    └── exception/
```

### Regras de Arquitetura

- **Controller não acessa repository**: Controllers apenas delegam para UseCases
- **Regras de negócio fora do controller**: Lógica de negócio no domínio
- **UseCases orquestram o fluxo**: Cada caso de uso tem uma responsabilidade única
- **Domínio independente de framework**: Entidades sem anotações JPA/Spring

## Funcionalidades

### CRUD de Produtos

- **POST** `/products` - Criar produto
- **GET** `/products/{id}` - Buscar produto por ID
- **GET** `/products` - Listar produtos (com paginação)
- **PUT** `/products/{id}` - Atualizar produto
- **DELETE** `/products/{id}` - Deletar produto

### Paginação

O endpoint `GET /products` implementa **paginação obrigatória** para melhorar a performance com grandes volumes de dados.

**Parâmetros de Query:**
- `page` (opcional, padrão: `0`) - Número da página (começa em 0)
- `size` (opcional, padrão: `20`) - Quantidade de itens por página (máximo: 100)

**Exemplo:**
```
GET /products?page=0&size=20
GET /products?page=2&size=50
```

**Resposta Paginada:**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Notebook",
      "description": "Notebook Dell Inspiron 15",
      "price": 3500.00,
      "stockQuantity": 10
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

### Campos do Produto

- `id` (UUID)
- `name` (String)
- `description` (String)
- `price` (BigDecimal)
- `stockQuantity` (Integer)

## Executando Localmente

### Pré-requisitos

- Java 21
- Maven 3.6+
- PostgreSQL 16+ (ou Docker)

### Configuração do Banco de Dados

Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE product_db;
```

### Variáveis de Ambiente (Opcional)

```bash
export DATASOURCE_URL=jdbc:postgresql://localhost:5432/product_db
export DATASOURCE_USERNAME=postgres
export DATASOURCE_PASSWORD=postgres
export SERVER_PORT=8081
```

### Executando com Maven

```bash
./mvnw spring-boot:run
```

O serviço estará disponível em `http://localhost:8081`

## Executando com Docker

### Docker Compose

```bash
docker-compose up -d
```

Isso irá:
- Subir um container PostgreSQL
- Subir um container Redis
- Subir o serviço product-service
- Executar as migrações do Flyway automaticamente

### Apenas Docker

```bash
# Build da imagem
docker build -t product-service .

# Executar (requer PostgreSQL rodando)
docker run -p 8081:8081 \
  -e DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/product_db \
  -e DATASOURCE_USERNAME=postgres \
  -e DATASOURCE_PASSWORD=postgres \
  product-service
```

## Exemplos de Uso

### Criar Produto

```bash
curl -X POST http://localhost:8081/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook",
    "description": "Notebook Dell Inspiron 15",
    "price": 3500.00,
    "stockQuantity": 10
  }'
```

### Buscar Produto

```bash
curl http://localhost:8081/products/{id}
```

### Listar Produtos

**Sem paginação (usa valores padrão: page=0, size=20):**
```bash
curl http://localhost:8081/products
```

**Com paginação:**
```bash
# Primeira página com 10 itens
curl "http://localhost:8081/products?page=0&size=10"

# Segunda página com 50 itens
curl "http://localhost:8081/products?page=1&size=50"
```

### Atualizar Produto

```bash
curl -X PUT http://localhost:8081/products/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Atualizado",
    "description": "Nova descrição",
    "price": 3800.00,
    "stockQuantity": 15
  }'
```

### Deletar Produto

```bash
curl -X DELETE http://localhost:8081/products/{id}
```

## Tratamento de Erros

O serviço possui tratamento centralizado de exceções com:

- **ResourceNotFoundException**: Recurso não encontrado (404)
- **BusinessException**: Erros de negócio (400)
- **ValidationException**: Erros de validação (400)
- **GlobalExceptionHandler**: Tratamento genérico (500)

Todos os erros retornam um formato consistente:

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Product with id xxx not found",
  "path": "/products/xxx"
}
```

## Cache com Redis

O serviço implementa **cache distribuído com Redis** para melhorar a performance e reduzir a carga no banco de dados PostgreSQL.

### Estratégias de Cache

1. **Cache por ID de Produto**
   - **Chave no Redis:** `product-service:product:{uuid}`
   - **TTL:** 5 minutos (300 segundos)
   - **Aplicado em:** `GET /products/{id}`
   - **Comportamento:** Primeira requisição busca no banco e armazena no cache; requisições subsequentes retornam do cache

2. **Cache Paginado**
   - **Chave no Redis:** `product-service:productsPage:page:{page}:size:{size}`
   - **TTL:** 3 minutos (180 segundos)
   - **Aplicado em:** `GET /products?page=X&size=Y`
   - **Comportamento:** Cada combinação de página/tamanho é cacheada independentemente

3. **Invalidação Automática**
   - **CREATE:** Invalida todas as páginas do cache de listagem (`@CacheEvict` em `CreateProductUseCase`)
   - **UPDATE:** Invalida cache do produto específico e todas as páginas (`@CacheEvict` em `UpdateProductUseCase`)
   - **DELETE:** Invalida cache do produto específico e todas as páginas (`@CacheEvict` em `DeleteProductUseCase`)

### Configuração Redis

**Variáveis de Ambiente:**
- `REDIS_HOST` (padrão: `localhost`)
- `REDIS_PORT` (padrão: `6379`)
- `REDIS_PASSWORD` (opcional)

**Docker Compose:**
O Redis é iniciado automaticamente junto com o serviço via `docker-compose.yml`:
- Imagem: `redis:7-alpine`
- Porta: `6379`
- Persistência: Volume `redis_data` com AOF habilitado

### Serialização

O cache utiliza **`Jackson2JsonRedisSerializer`** (recomendado pelo Spring Data Redis 4.0) para serialização JSON:
- **Chaves:** `StringRedisSerializer`
- **Valores:** `Jackson2JsonRedisSerializer<Object>` com type information
- Suporta tipos genéricos como `PageResponse<ProductResponse>`

### Benefícios

- ⚡ **Performance**: Respostas até 10x mais rápidas em cache hits (< 5ms vs ~50-100ms)
- 📉 **Redução de Carga**: 70-90% menos queries no PostgreSQL em cenários de alta leitura
- 🔄 **Transparência**: Cache é transparente para consumidores da API
- 📈 **Escalabilidade**: Redis suporta alta concorrência e múltiplas instâncias

### Testando o Cache

**1. Verificar Cache Hit/Miss nos Logs:**
```bash
# Primeira requisição (MISS - busca no banco)
curl http://localhost:8081/products/{id}

# Segunda requisição (HIT - retorna do cache)
curl http://localhost:8081/products/{id}
```

**2. Verificar no Redis CLI:**
```bash
docker exec -it product-service-redis redis-cli

# Listar todas as chaves do cache
KEYS product-service:*

# Ver valor de uma chave específica
GET product-service:product:{uuid}

# Ver TTL de uma chave
TTL product-service:product:{uuid}
```

**3. Limpar Cache Manualmente:**
```bash
docker exec -it product-service-redis redis-cli FLUSHDB
```

## Logs

O serviço utiliza SLF4J para logging estruturado:

- Erros são logados com contexto da requisição
- Stack traces nunca são expostos ao cliente
- Níveis de log configuráveis via variáveis de ambiente
- Logs de cache (cache hit/miss) em nível DEBUG

## Migrações de Banco de Dados

As migrações são gerenciadas pelo Flyway e estão localizadas em:
`src/main/resources/db/migration/`

A migração inicial cria a tabela `products` com todas as constraints necessárias.

## Importação de Produtos em Massa

O projeto inclui um script para importar múltiplos produtos de uma vez a partir de um arquivo JSON.

### Pré-requisitos

- Serviço rodando (localmente ou via Docker)
- Arquivo `products-sample.json` na raiz do projeto
- Ferramenta `jq` instalada (para processar JSON)

**Instalar jq:**
```bash
# Ubuntu/Debian
sudo apt-get install jq

# macOS
brew install jq

# Ou via Docker
docker run --rm -v $(pwd):/data imega/jq
```

### Executando a Importação

**1. Garantir que o serviço está rodando:**
```bash
# Com Docker Compose
docker-compose up -d

# Ou localmente
./mvnw spring-boot:run
```

**2. Executar o script de importação:**
```bash
# Dar permissão de execução (primeira vez)
chmod +x import-products.sh

# Executar o script
./import-products.sh
```

### Formato do Arquivo JSON

O arquivo `products-sample.json` deve seguir o formato:
```json
[
  {
    "name": "Produto 1",
    "description": "Descrição do produto 1",
    "price": 100.00,
    "stockQuantity": 10
  },
  {
    "name": "Produto 2",
    "description": "Descrição do produto 2",
    "price": 200.00,
    "stockQuantity": 20
  }
]
```

### Personalizando a Importação

Você pode modificar o script `import-products.sh` para:
- Alterar a URL base (padrão: `http://localhost:8081`)
- Usar um arquivo JSON diferente
- Adicionar autenticação se necessário

**Exemplo com arquivo customizado:**
```bash
JSON_FILE="meus-produtos.json" ./import-products.sh
```

### Resultado

O script exibe:
- Progresso de cada produto sendo importado
- Status de sucesso (✓) ou erro (✗)
- Resumo final com total processado, sucessos e erros

**Exemplo de saída:**
```
Importando produtos de products-sample.json...

Cadastrando produto 1...
✓ Produto cadastrado: Notebook

Cadastrando produto 2...
✓ Produto cadastrado: Mouse

==========================================
Importação concluída!
Total processado: 40
Sucessos: 40
Erros: 0
==========================================
```

**Nota:** Após a importação, o cache de listagem será automaticamente invalidado nas próximas operações de CREATE/UPDATE/DELETE.

## Testes

```bash
./mvnw test
```

**Cobertura de Testes:**
- ✅ Testes unitários para todos os UseCases
- ✅ Testes de paginação
- ✅ Testes de comportamento de cache
- ✅ Testes de repositório com paginação
- ✅ Testes de controller e exception handlers

## Porta

O serviço roda na porta **8081** por padrão, configurável via `SERVER_PORT`.

## Paginação - Guia de Integração

### Para Consumidores da API

O endpoint `GET /products` agora retorna um objeto `PageResponse` ao invés de uma lista simples. Isso é **obrigatório** para melhorar a performance.

**Estrutura da Resposta:**
- `content`: Array de produtos da página atual
- `page`: Número da página atual (0-indexed)
- `size`: Tamanho da página
- `totalElements`: Total de produtos no banco
- `totalPages`: Total de páginas disponíveis
- `first`: `true` se é a primeira página
- `last`: `true` se é a última página

**Validações:**
- `page` deve ser >= 0
- `size` deve estar entre 1 e 100

### Carregando Todas as Páginas

Para serviços que precisam carregar todos os produtos (ex: stock-query-service):

```java
List<ProductResponse> allProducts = new ArrayList<>();
int page = 0;
int size = 20;
boolean hasMore = true;

while (hasMore) {
    PageResponse<ProductResponse> response = 
        productServiceClient.getProducts(page, size);
    
    allProducts.addAll(response.getContent());
    hasMore = !response.isLast();
    page++;
}
```




