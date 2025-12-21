#!/bin/bash

# Script para importar produtos do arquivo JSON
# Uso: ./import-products.sh

BASE_URL="http://localhost:8081"
JSON_FILE="products-sample.json"

echo "Importando produtos de $JSON_FILE..."
echo ""

# Contador
count=0
success=0
errors=0

# Lê o arquivo JSON e processa cada produto
jq -c '.[]' "$JSON_FILE" | while read -r product; do
    count=$((count + 1))
    
    echo "Cadastrando produto $count..."
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/products" \
        -H "Content-Type: application/json" \
        -d "$product")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -eq 201 ]; then
        success=$((success + 1))
        product_name=$(echo "$product" | jq -r '.name')
        echo "✓ Produto cadastrado: $product_name"
    else
        errors=$((errors + 1))
        echo "✗ Erro ao cadastrar produto (HTTP $http_code)"
        echo "  Resposta: $body"
    fi
    echo ""
done

echo "=========================================="
echo "Importação concluída!"
echo "Total processado: $count"
echo "Sucessos: $success"
echo "Erros: $errors"
echo "=========================================="


