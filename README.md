# Product Service

Serviço de gerenciamento de produtos desenvolvido com Spring Boot 4, seguindo princípios de Clean Architecture e arquitetura hexagonal.

## Stack Tecnológica

- **Java 21**
- **Spring Boot 4.0.1**
- **PostgreSQL**
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

## Logs

O serviço utiliza SLF4J para logging estruturado:

- Erros são logados com contexto da requisição
- Stack traces nunca são expostos ao cliente
- Níveis de log configuráveis via variáveis de ambiente

## Migrações de Banco de Dados

As migrações são gerenciadas pelo Flyway e estão localizadas em:
`src/main/resources/db/migration/`

A migração inicial cria a tabela `products` com todas as constraints necessárias.

## Testes

```bash
./mvnw test
```

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

### Documentação Completa

Para exemplos detalhados de implementação em Java (Spring Boot/Feign) e TypeScript (Angular), consulte o arquivo **`PAGINATION_GUIDE.md`**.

## Próximos Passos

Este serviço está preparado para ser consumido pelo **stock-query-service**, que consultará informações de produtos e estoque através das APIs REST expostas.

**Importante:** O stock-query-service e o frontend precisam ser atualizados para trabalhar com paginação. Consulte `PAGINATION_GUIDE.md` para detalhes de implementação.




