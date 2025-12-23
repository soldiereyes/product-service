package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.application.mapper.ProductMapper;
import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.api.dto.CreateProductRequest;
import com.techsolution.product_service.api.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    private CreateProductRequest request;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        request = new CreateProductRequest(
                "Notebook",
                "Notebook Dell Inspiron 15",
                new BigDecimal("3500.00"),
                10
        );

        UUID productId = UUID.randomUUID();
        savedProduct = new Product(
                productId,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );
    }

    @Test
    void shouldCreateProductSuccessfully() {
        ProductResponse expectedResponse = new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice(),
                savedProduct.getStockQuantity()
        );

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(expectedResponse);

        ProductResponse response = createProductUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(savedProduct.getId());
        assertThat(response.name()).isEqualTo(request.name());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.price()).isEqualTo(request.price());
        assertThat(response.stockQuantity()).isEqualTo(request.stockQuantity());

        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(savedProduct);
    }

    @Test
    void shouldCreateProductWithZeroStock() {
        CreateProductRequest zeroStockRequest = new CreateProductRequest(
                "Product",
                "Description",
                new BigDecimal("100.00"),
                0
        );

        UUID productId = UUID.randomUUID();
        Product productWithZeroStock = new Product(
                productId,
                zeroStockRequest.name(),
                zeroStockRequest.description(),
                zeroStockRequest.price(),
                zeroStockRequest.stockQuantity()
        );

        ProductResponse expectedResponse = new ProductResponse(
                productId,
                zeroStockRequest.name(),
                zeroStockRequest.description(),
                zeroStockRequest.price(),
                zeroStockRequest.stockQuantity()
        );

        when(productRepository.save(any(Product.class))).thenReturn(productWithZeroStock);
        when(productMapper.toResponse(productWithZeroStock)).thenReturn(expectedResponse);

        ProductResponse response = createProductUseCase.execute(zeroStockRequest);

        assertThat(response.stockQuantity()).isZero();
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(productWithZeroStock);
    }
}




