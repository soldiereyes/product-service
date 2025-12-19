package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.domain.exception.ResourceNotFoundException;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductByIdUseCase getProductByIdUseCase;

    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product(
                productId,
                "Notebook",
                "Notebook Dell Inspiron 15",
                new BigDecimal("3500.00"),
                10
        );
    }

    @Test
    void shouldGetProductByIdSuccessfully() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = getProductByIdUseCase.execute(productId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(productId);
        assertThat(response.name()).isEqualTo(product.getName());
        assertThat(response.description()).isEqualTo(product.getDescription());
        assertThat(response.price()).isEqualTo(product.getPrice());
        assertThat(response.stockQuantity()).isEqualTo(product.getStockQuantity());

        verify(productRepository).findById(productId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProductByIdUseCase.execute(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product")
                .hasMessageContaining(productId.toString());

        verify(productRepository).findById(productId);
    }
}



