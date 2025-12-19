package com.techsolution.product_service.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void shouldCreateProductWithAllFields() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "Notebook", "Description", new BigDecimal("1000.00"), 10);

        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo("Notebook");
        assertThat(product.getDescription()).isEqualTo("Description");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }

    @Test
    void shouldGenerateIdWhenCreatingProductWithoutId() {
        Product product = new Product("Notebook", "Description", new BigDecimal("1000.00"), 10);

        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo("Notebook");
    }

    @Test
    void shouldAllowZeroStockQuantity() {
        Product product = new Product("Product", "Description", new BigDecimal("100.00"), 0);

        assertThat(product.getStockQuantity()).isZero();
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThatThrownBy(() -> new Product(null, "Description", new BigDecimal("100.00"), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThatThrownBy(() -> new Product("", "Description", new BigDecimal("100.00"), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThatThrownBy(() -> new Product("   ", "Description", new BigDecimal("100.00"), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        assertThatThrownBy(() -> new Product("Name", null, new BigDecimal("100.00"), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("description cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        assertThatThrownBy(() -> new Product("Name", "", new BigDecimal("100.00"), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("description cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        assertThatThrownBy(() -> new Product("Name", "Description", null, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price cannot be null or negative");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThatThrownBy(() -> new Product("Name", "Description", new BigDecimal("-100.00"), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price cannot be null or negative");
    }

    @Test
    void shouldAllowZeroPrice() {
        Product product = new Product("Name", "Description", BigDecimal.ZERO, 10);
        assertThat(product.getPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldThrowExceptionWhenStockQuantityIsNull() {
        assertThatThrownBy(() -> new Product("Name", "Description", new BigDecimal("100.00"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock quantity cannot be null or negative");
    }

    @Test
    void shouldThrowExceptionWhenStockQuantityIsNegative() {
        assertThatThrownBy(() -> new Product("Name", "Description", new BigDecimal("100.00"), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock quantity cannot be null or negative");
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product product = new Product("Old Name", "Old Description", new BigDecimal("100.00"), 5);

        product.update("New Name", "New Description", new BigDecimal("200.00"), 10);

        assertThat(product.getName()).isEqualTo("New Name");
        assertThat(product.getDescription()).isEqualTo("New Description");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("200.00"));
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }

    @Test
    void shouldValidateOnUpdate() {
        Product product = new Product("Name", "Description", new BigDecimal("100.00"), 5);

        assertThatThrownBy(() -> product.update(null, "Description", new BigDecimal("100.00"), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name cannot be null or empty");
    }

    @Test
    void shouldSetId() {
        Product product = new Product("Name", "Description", new BigDecimal("100.00"), 5);
        UUID newId = UUID.randomUUID();

        product.setId(newId);

        assertThat(product.getId()).isEqualTo(newId);
    }
}

