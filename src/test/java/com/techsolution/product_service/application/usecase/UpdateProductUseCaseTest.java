package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.application.mapper.ProductMapper;
import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.domain.exception.ResourceNotFoundException;
import com.techsolution.product_service.api.dto.ProductResponse;
import com.techsolution.product_service.api.dto.UpdateProductRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private UpdateProductUseCase updateProductUseCase;

    private UUID productId;
    private Product existingProduct;
    private UpdateProductRequest request;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        existingProduct = new Product(
                productId,
                "Old Name",
                "Old Description",
                new BigDecimal("1000.00"),
                5
        );

        request = new UpdateProductRequest(
                "New Name",
                "New Description",
                new BigDecimal("2000.00"),
                15
        );
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product updatedProduct = new Product(
                productId,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );
        ProductResponse expectedResponse = new ProductResponse(
                productId,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any(Product.class))).thenReturn(expectedResponse);

        ProductResponse response = updateProductUseCase.execute(productId, request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(productId);
        assertThat(response.name()).isEqualTo(request.name());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.price()).isEqualTo(request.price());
        assertThat(response.stockQuantity()).isEqualTo(request.stockQuantity());

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProductUseCase.execute(productId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product")
                .hasMessageContaining(productId.toString());

        verify(productRepository).findById(productId);
        verify(productRepository, org.mockito.Mockito.never()).save(any(Product.class));
    }
}




