-- Adiciona coluna active para suportar desativação de produtos ao invés de exclusão
ALTER TABLE products 
ADD COLUMN active BOOLEAN NOT NULL DEFAULT true;

-- Cria índice para melhorar performance de queries que filtram por produtos ativos
CREATE INDEX idx_products_active ON products(active);

