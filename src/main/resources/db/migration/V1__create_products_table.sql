CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    CONSTRAINT price_positive CHECK (price > 0),
    CONSTRAINT stock_quantity_non_negative CHECK (stock_quantity >= 0)
);

CREATE INDEX idx_products_name ON products(name);

