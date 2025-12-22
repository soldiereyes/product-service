# Implementação de Cache Redis

## Visão Geral

O product-service agora utiliza **Redis** como camada de cache distribuído para melhorar a performance e reduzir a carga no banco de dados PostgreSQL.

## Configuração

### Dependências Adicionadas

- `spring-boot-starter-data-redis` - Cliente Redis
- `spring-boot-starter-cache` - Abstração de cache do Spring

### Serialização

O cache utiliza **`Jackson2JsonRedisSerializer`** (substituição recomendada para `GenericJackson2JsonRedisSerializer` que foi depreciada no Spring Data Redis 4.0) para serialização JSON dos objetos em cache.

**Configuração:**
- Serialização de chaves: `StringRedisSerializer`
- Serialização de valores: `Jackson2JsonRedisSerializer<Object>` com type information
- Suporta tipos genéricos como `PageResponse<ProductResponse>`

### Configuração Redis

**application.yml:**
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0

cache:
  redis:
    time-to-live: 300000
    cache-null-values: false
    use-key-prefix: true
    key-prefix: "product-service:"
```

### Docker Compose

O Redis é iniciado automaticamente via `docker-compose.yml`:

```yaml
redis:
  image: redis:7-alpine
  container_name: product-service-redis
  ports:
    - "${REDIS_PORT:-6379}:6379"
  command: redis-server --appendonly yes
  volumes:
    - redis_data:/data
```

## Estratégias de Cache Implementadas

### 1. Cache por ID de Produto

**UseCase:** `GetProductByIdUseCase`

**Anotação:**
```java
@Cacheable(value = "product", key = "#id.toString()", unless = "#result == null")
```

**Chave no Redis:**
```
product-service:product:{uuid}
```

**TTL:** 5 minutos (300 segundos)

**Comportamento:**
- Primeira requisição: busca no banco → armazena no cache
- Requisições subsequentes: retorna do cache (sem acessar banco)

### 2. Cache Paginado

**UseCase:** `ListProductsUseCase`

**Anotação:**
```java
@Cacheable(value = "productsPage", key = "'page:' + #page + ':size:' + #size", unless = "#result == null")
```

**Chave no Redis:**
```
product-service:productsPage:page:0:size:20
```

**TTL:** 3 minutos (180 segundos)

**Comportamento:**
- Cache por combinação de página e tamanho
- Cada página é cacheada independentemente

### 3. Invalidação de Cache

#### CREATE (CreateProductUseCase)
```java
@CacheEvict(value = "productsPage", allEntries = true)
```
- Remove todas as páginas do cache de listagem
- Novo produto não é cacheado individualmente (será cacheado quando buscado por ID)

#### UPDATE (UpdateProductUseCase)
```java
@CacheEvict(value = {"product", "productsPage"}, key = "#id.toString()", allEntries = true)
```
- Remove o produto específico do cache
- Remove todas as páginas do cache de listagem
- Próxima busca por ID buscará do banco e recacheará

#### DELETE (DeleteProductUseCase)
```java
@CacheEvict(value = {"product", "productsPage"}, key = "#id.toString()", allEntries = true)
```
- Remove o produto específico do cache
- Remove todas as páginas do cache de listagem

## Fluxo de Cache

### Cache HIT (Produto no Cache)
```
Requisição → Controller → UseCase → Redis (HIT) → Resposta
                                    ↑
                              Sem acesso ao banco
```

### Cache MISS (Produto não no Cache)
```
Requisição → Controller → UseCase → Redis (MISS) → Repository → Banco → Redis (SAVE) → Resposta
```

### Invalidação (Update/Delete)
```
Requisição → Controller → UseCase → Repository → Banco → Redis (DELETE) → Resposta
```

## Logs de Cache

Os logs de cache são registrados em nível DEBUG:

```
DEBUG GetProductByIdUseCase - Executing GetProductByIdUseCase for product id: {id} (cache miss)
DEBUG GetProductByIdUseCase - Product found with id: {id} - caching result
```

## Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `REDIS_HOST` | `localhost` | Host do Redis |
| `REDIS_PORT` | `6379` | Porta do Redis |
| `REDIS_PASSWORD` | (vazio) | Senha do Redis (opcional) |

## Executando Localmente

### Com Docker Compose (Recomendado)
```bash
docker-compose up -d
```

Isso inicia:
- PostgreSQL
- Redis
- Product Service

### Sem Docker

1. **Iniciar Redis:**
```bash
docker run -d -p 6379:6379 redis:7-alpine
```

2. **Iniciar Product Service:**
```bash
./mvnw spring-boot:run
```

## Testando o Cache

### 1. Verificar Cache Hit/Miss nos Logs

Primeira requisição (MISS):
```bash
curl http://localhost:8081/products/{id}
# Log: "cache miss"
```

Segunda requisição (HIT):
```bash
curl http://localhost:8081/products/{id}
# Log: "cache hit" (mais rápido)
```

### 2. Verificar no Redis CLI

```bash
docker exec -it product-service-redis redis-cli

# Listar todas as chaves
KEYS product-service:*

# Ver valor de uma chave
GET product-service:product:{uuid}

# Ver TTL de uma chave
TTL product-service:product:{uuid}
```

### 3. Limpar Cache Manualmente

```bash
docker exec -it product-service-redis redis-cli FLUSHDB
```

## Monitoramento

### Health Check

O Redis é verificado automaticamente pelo healthcheck do Docker Compose.

### Métricas (Futuro)

Pode-se adicionar métricas de cache via Spring Actuator:
- Cache hits/misses
- Tamanho do cache
- Taxa de acerto

## Performance Esperada

- **Cache HIT**: < 5ms (resposta do Redis)
- **Cache MISS**: ~50-100ms (busca no banco + cache)
- **Redução de carga no banco**: 70-90% em cenários de alta leitura

## Troubleshooting

### Redis não conecta

1. Verificar se Redis está rodando:
```bash
docker ps | grep redis
```

2. Verificar logs:
```bash
docker logs product-service-redis
```

3. Testar conexão:
```bash
docker exec -it product-service-redis redis-cli ping
# Deve retornar: PONG
```

### Cache não funciona

1. Verificar se `@EnableCaching` está na classe principal
2. Verificar se Redis está configurado corretamente
3. Verificar logs em nível DEBUG

### Cache sempre MISS

1. Verificar TTL (chaves podem estar expirando muito rápido)
2. Verificar se invalidação não está sendo muito agressiva
3. Verificar se Redis está realmente armazenando dados

## Boas Práticas Implementadas

✅ **TTL apropriado**: 5min para produtos, 3min para listagens  
✅ **Invalidação automática**: Cache sempre consistente após mudanças  
✅ **Serialização JSON**: DTOs serializados corretamente  
✅ **Pool de conexões**: Configurado para alta concorrência  
✅ **Testes sem Redis**: Testes unitários não dependem de Redis  

## Próximos Passos (Opcional)

- [ ] Adicionar métricas de cache via Actuator
- [ ] Implementar cache warming (pré-carregamento)
- [ ] Adicionar cache de contadores (total de produtos)
- [ ] Implementar cache distribuído para múltiplas instâncias

