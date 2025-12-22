package com.techsolution.product_service.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean active;

    public Product() {
    }

    public Product(UUID id, String name, String description, BigDecimal price, Integer stockQuantity, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.active = active != null ? active : true;
        validate();
    }

    public Product(String name, String description, BigDecimal price, Integer stockQuantity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.active = true;
        validate();
    }

    public Product(UUID id, String name, String description, BigDecimal price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.active = true;
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.DESCRIPTION_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(ProductValidationMessages.PRICE_CANNOT_BE_NULL_OR_NEGATIVE);
        }
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException(ProductValidationMessages.STOCK_QUANTITY_CANNOT_BE_NULL_OR_NEGATIVE);
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active != null ? active : true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active != null && active;
    }
}
