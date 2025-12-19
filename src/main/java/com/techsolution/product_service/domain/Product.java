package com.techsolution.product_service.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;

    public Product() {
    }

    public Product(UUID id, String name, String description, BigDecimal price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        validate();
    }

    public Product(String name, String description, BigDecimal price, Integer stockQuantity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be null or negative");
        }
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Product stock quantity cannot be null or negative");
        }
    }

    public void update(String name, String description, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        validate();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
}

