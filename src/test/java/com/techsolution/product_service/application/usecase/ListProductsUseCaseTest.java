package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.application.mapper.ProductMapper;
import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.interfaces.dto.PageResponse;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ListProductsUseCase listProductsUseCase;

    private List<Product> products;

    @BeforeEach
    void setUp() {
        Product product1 = new Product(
                UUID.randomUUID(),
                "Notebook",
                "Notebook Dell Inspiron 15",
                new BigDecimal("3500.00"),
                10
        );

        Product product2 = new Product(
                UUID.randomUUID(),
                "Mouse",
                "Mouse Logitech",
                new BigDecimal("50.00"),
                20
        );

        products = Arrays.asList(product1, product2);
    }

    @Test
    void shouldListAllProductsSuccessfully() {
        List<ProductResponse> expectedResponses = Arrays.asList(
                new ProductResponse(products.get(0).getId(), products.get(0).getName(), 
                        products.get(0).getDescription(), products.get(0).getPrice(), 
                        products.get(0).getStockQuantity()),
                new ProductResponse(products.get(1).getId(), products.get(1).getName(), 
                        products.get(1).getDescription(), products.get(1).getPrice(), 
                        products.get(1).getStockQuantity())
        );

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponseList(products)).thenReturn(expectedResponses);

        List<ProductResponse> response = listProductsUseCase.execute();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);
        assertThat(response.get(0).name()).isEqualTo(products.get(0).getName());
        assertThat(response.get(1).name()).isEqualTo(products.get(1).getName());

        verify(productRepository).findAll();
        verify(productMapper).toResponseList(products);
    }

    @Test
    void shouldReturnEmptyListWhenNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());
        when(productMapper.toResponseList(List.of())).thenReturn(List.of());

        List<ProductResponse> response = listProductsUseCase.execute();

        assertThat(response).isNotNull();
        assertThat(response).isEmpty();

        verify(productRepository).findAll();
        verify(productMapper).toResponseList(List.of());
    }

    @Test
    void shouldListProductsWithPagination() {
        int page = 0;
        int size = 20;
        ProductRepository.PageResult<Product> pageResult = new ProductRepository.PageResult<>(
                products, 2L, 1
        );
        List<ProductResponse> expectedResponses = Arrays.asList(
                new ProductResponse(products.get(0).getId(), products.get(0).getName(), 
                        products.get(0).getDescription(), products.get(0).getPrice(), 
                        products.get(0).getStockQuantity()),
                new ProductResponse(products.get(1).getId(), products.get(1).getName(), 
                        products.get(1).getDescription(), products.get(1).getPrice(), 
                        products.get(1).getStockQuantity())
        );

        when(productRepository.findAll(page, size)).thenReturn(pageResult);
        when(productMapper.toResponseList(products)).thenReturn(expectedResponses);

        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);
        assertThat(response.totalElements()).isEqualTo(2L);
        assertThat(response.totalPages()).isEqualTo(1);

        verify(productRepository).findAll(page, size);
        verify(productMapper).toResponseList(products);
    }

    @Test
    void shouldListProductsWithPaginationSuccessfully() {
        int page = 0;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;

        ProductRepository.PageResult<Product> pageResult = new ProductRepository.PageResult<>(
                products,
                totalElements,
                totalPages
        );

        when(productRepository.findAll(page, size)).thenReturn(pageResult);

        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);
        assertThat(response.page()).isEqualTo(page);
        assertThat(response.size()).isEqualTo(size);
        assertThat(response.totalElements()).isEqualTo(totalElements);
        assertThat(response.totalPages()).isEqualTo(totalPages);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();

        verify(productRepository).findAll(page, size);
    }

    @Test
    void shouldReturnEmptyPageWhenNoProductsWithPagination() {
        int page = 0;
        int size = 10;

        ProductRepository.PageResult<Product> pageResult = new ProductRepository.PageResult<>(
                List.of(),
                0L,
                0
        );

        when(productRepository.findAll(page, size)).thenReturn(pageResult);

        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);

        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
        assertThat(response.totalPages()).isZero();
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();

        verify(productRepository).findAll(page, size);
    }

    @Test
    void shouldHandleLastPageCorrectly() {
        int page = 2;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;

        ProductRepository.PageResult<Product> pageResult = new ProductRepository.PageResult<>(
                products,
                totalElements,
                totalPages
        );

        when(productRepository.findAll(page, size)).thenReturn(pageResult);

        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);

        assertThat(response).isNotNull();
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
        assertThat(response.page()).isEqualTo(page);

        verify(productRepository).findAll(page, size);
    }

    @Test
    void shouldListProductsWithPaginationSuccessfully() {
        int page = 0;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;

        ProductRepository.PageResult<Product> pageResult = new ProductRepository.PageResult<>(
                products,
                totalElements,
                totalPages
        );

        when(productRepository.findAll(page, size)).thenReturn(pageResult);

        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);
        assertThat(response.page()).isEqualTo(page);
        assertThat(response.size()).isEqualTo(size);
        assertThat(response.totalElements()).isEqualTo(totalElements);
        assertThat(response.totalPages()).isEqualTo(totalPages);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();

        verify(productRepository).findAll(page, size);
    }

    @Test
    void shouldReturnEmptyPageWhenNoProductsWithPagination() {
        int page = 0;
        int size = 10;

        ProductRepository.PageResult<Product> pageResult = new ProductRepository.PageResult<>(
                List.of(),
                0L,
                0
        );

        when(productRepository.findAll(page, size)).thenReturn(pageResult);

        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);

        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
        assertThat(response.totalPages()).isZero();
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();

        verify(productRepository).findAll(page, size);
    }

    @Test
    void shouldHandleLastPageCorrectly() {
        int page = 2;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;

        ProductRepository.PageResult<Product> pageResult = new ProductRepository.PageResult<>(
                products,
                totalElements,
                totalPages
        );

        when(productRepository.findAll(page, size)).thenReturn(pageResult);

        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);

        assertThat(response).isNotNull();
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
        assertThat(response.page()).isEqualTo(page);

        verify(productRepository).findAll(page, size);
    }
}



