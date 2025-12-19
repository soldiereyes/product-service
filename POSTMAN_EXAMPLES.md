# Exemplos de Requisições para Postman

## Base URL
```
http://localhost:8081
```

---

## 1. POST /products - Criar Produto

**Endpoint:** `POST http://localhost:8081/products`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "name": "Notebook Dell Inspiron 15",
  "description": "Notebook Dell Inspiron 15 3000, Intel Core i5, 8GB RAM, 256GB SSD",
  "price": 3500.00,
  "stockQuantity": 10
}
```

**Exemplo 2 - Produto com estoque zero:**
```json
{
  "name": "Mouse Logitech MX Master 3",
  "description": "Mouse sem fio Logitech MX Master 3, recarregável",
  "price": 450.00,
  "stockQuantity": 0
}
```

**Exemplo 3 - Produto com preço alto:**
```json
{
  "name": "MacBook Pro 16",
  "description": "MacBook Pro 16 polegadas, M3 Pro, 18GB RAM, 512GB SSD",
  "price": 15999.99,
  "stockQuantity": 3
}
```

**Resposta de Sucesso (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Notebook Dell Inspiron 15",
  "description": "Notebook Dell Inspiron 15 3000, Intel Core i5, 8GB RAM, 256GB SSD",
  "price": 3500.00,
  "stockQuantity": 10
}
```

**Resposta de Erro - Validação (400 Bad Request):**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Validation failed: {name=Name is required, price=Price must be greater than zero}",
  "path": "/products"
}
```

---

## 2. GET /products/{id} - Buscar Produto por ID

**Endpoint:** `GET http://localhost:8081/products/{id}`

**Exemplo:**
```
GET http://localhost:8081/products/550e8400-e29b-41d4-a716-446655440000
```

**Resposta de Sucesso (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Notebook Dell Inspiron 15",
  "description": "Notebook Dell Inspiron 15 3000, Intel Core i5, 8GB RAM, 256GB SSD",
  "price": 3500.00,
  "stockQuantity": 10
}
```

**Resposta de Erro - Não Encontrado (404 Not Found):**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Product with id 550e8400-e29b-41d4-a716-446655440000 not found",
  "path": "/products/550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 3. GET /products - Listar Todos os Produtos

**Endpoint:** `GET http://localhost:8081/products`

**Resposta de Sucesso (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Notebook Dell Inspiron 15",
    "description": "Notebook Dell Inspiron 15 3000, Intel Core i5, 8GB RAM, 256GB SSD",
    "price": 3500.00,
    "stockQuantity": 10
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "name": "Mouse Logitech MX Master 3",
    "description": "Mouse sem fio Logitech MX Master 3, recarregável",
    "price": 450.00,
    "stockQuantity": 0
  },
  {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "name": "MacBook Pro 16",
    "description": "MacBook Pro 16 polegadas, M3 Pro, 18GB RAM, 512GB SSD",
    "price": 15999.99,
    "stockQuantity": 3
  }
]
```

**Resposta quando não há produtos (200 OK):**
```json
[]
```

---

## 4. PUT /products/{id} - Atualizar Produto

**Endpoint:** `PUT http://localhost:8081/products/{id}`

**Headers:**
```
Content-Type: application/json
```

**Exemplo:**
```
PUT http://localhost:8081/products/550e8400-e29b-41d4-a716-446655440000
```

**Body (JSON):**
```json
{
  "name": "Notebook Dell Inspiron 15 - Atualizado",
  "description": "Notebook Dell Inspiron 15 3000, Intel Core i5, 16GB RAM, 512GB SSD - Modelo 2024",
  "price": 4200.00,
  "stockQuantity": 15
}
```

**Resposta de Sucesso (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Notebook Dell Inspiron 15 - Atualizado",
  "description": "Notebook Dell Inspiron 15 3000, Intel Core i5, 16GB RAM, 512GB SSD - Modelo 2024",
  "price": 4200.00,
  "stockQuantity": 15
}
```

**Resposta de Erro - Não Encontrado (404 Not Found):**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Product with id 550e8400-e29b-41d4-a716-446655440000 not found",
  "path": "/products/550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 5. DELETE /products/{id} - Deletar Produto

**Endpoint:** `DELETE http://localhost:8081/products/{id}`

**Exemplo:**
```
DELETE http://localhost:8081/products/550e8400-e29b-41d4-a716-446655440000
```

**Resposta de Sucesso (204 No Content):**
```
(sem corpo de resposta)
```

**Resposta de Erro - Não Encontrado (404 Not Found):**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Product with id 550e8400-e29b-41d4-a716-446655440000 not found",
  "path": "/products/550e8400-e29b-41d4-a716-446655440000"
}
```

---

## Exemplos de Erros de Validação

### Erro - Nome vazio:
```json
{
  "name": "",
  "description": "Descrição válida",
  "price": 100.00,
  "stockQuantity": 5
}
```

**Resposta:**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Validation failed: {name=Name is required}",
  "path": "/products"
}
```

### Erro - Preço negativo:
```json
{
  "name": "Produto",
  "description": "Descrição",
  "price": -100.00,
  "stockQuantity": 5
}
```

**Resposta:**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Validation failed: {price=Price must be greater than zero}",
  "path": "/products"
}
```

### Erro - Preço zero:
```json
{
  "name": "Produto",
  "description": "Descrição",
  "price": 0.00,
  "stockQuantity": 5
}
```

**Resposta:**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Validation failed: {price=Price must be greater than zero}",
  "path": "/products"
}
```

### Erro - Estoque negativo:
```json
{
  "name": "Produto",
  "description": "Descrição",
  "price": 100.00,
  "stockQuantity": -1
}
```

**Resposta:**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Validation failed: {stockQuantity=Stock quantity cannot be negative}",
  "path": "/products"
}
```

### Erro - Campos obrigatórios ausentes:
```json
{
  "name": "Produto"
}
```

**Resposta:**
```json
{
  "timestamp": "2024-12-19T14:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Validation failed: {description=Description is required, price=Price is required, stockQuantity=Stock quantity is required}",
  "path": "/products"
}
```

---

## Fluxo Completo de Teste

1. **Criar produto:**
   - POST /products com o primeiro JSON de exemplo
   - Copiar o `id` retornado

2. **Buscar produto criado:**
   - GET /products/{id} usando o id copiado

3. **Listar todos:**
   - GET /products

4. **Atualizar produto:**
   - PUT /products/{id} com o JSON de atualização

5. **Verificar atualização:**
   - GET /products/{id} novamente

6. **Deletar produto:**
   - DELETE /products/{id}

7. **Verificar exclusão:**
   - GET /products/{id} (deve retornar 404)

---

## Health Check

**Endpoint:** `GET http://localhost:8081/actuator/health`

**Resposta:**
```json
{
  "status": "UP"
}
```

