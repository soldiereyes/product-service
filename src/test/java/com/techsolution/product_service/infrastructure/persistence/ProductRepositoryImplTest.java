package com.techsolution.product_service.infrastructure.persistence;

import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.infrastructure.persistence.entity.ProductEntity;
import com.techsolution.product_service.infrastructure.persistence.jpa.JpaProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    @Mock
    private JpaProductRepository jpaProductRepository;

    @InjectMocks
    private ProductRepositoryImpl productRepositoryImpl;

    private UUID productId;
    private Product product;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product(
                productId,
                "Notebook",
                "Notebook Dell Inspiron 15",
                new BigDecimal("3500.00"),
                10,
                true
        );
        productEntity = new ProductEntity(
                productId,
                "Notebook",
                "Notebook Dell Inspiron 15",
                new BigDecimal("3500.00"),
                10,
                true
        );
    }

    @Test
    void shouldSaveProduct() {
        when(jpaProductRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        Product savedProduct = productRepositoryImpl.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isEqualTo(productId);
        assertThat(savedProduct.getName()).isEqualTo(product.getName());
        assertThat(savedProduct.getDescription()).isEqualTo(product.getDescription());
        assertThat(savedProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(savedProduct.getStockQuantity()).isEqualTo(product.getStockQuantity());

        verify(jpaProductRepository).save(any(ProductEntity.class));
    }

    @Test
    void shouldFindActiveProductById() {
        when(jpaProductRepository.findByIdAndActive(productId)).thenReturn(Optional.of(productEntity));

        Optional<Product> result = productRepositoryImpl.findById(productId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(productId);
        verify(jpaProductRepository).findByIdAndActive(productId);
    }

    @Test
    void shouldReturnEmptyWhenActiveProductNotFound() {
        when(jpaProductRepository.findByIdAndActive(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productRepositoryImpl.findById(productId);

        assertThat(result).isEmpty();
        verify(jpaProductRepository).findByIdAndActive(productId);
    }

    @Test
    void shouldFindAllActiveProducts() {
        ProductEntity entity2 = new ProductEntity(
                UUID.randomUUID(),
                "Mouse",
                "Mouse Logitech",
                new BigDecimal("50.00"),
                20,
                true
        );
        List<ProductEntity> entities = Arrays.asList(productEntity, entity2);
        when(jpaProductRepository.findAllActive()).thenReturn(entities);

        List<Product> products = productRepositoryImpl.findAll();

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getId()).isEqualTo(productId);
        assertThat(products.get(1).getName()).isEqualTo("Mouse");
        verify(jpaProductRepository).findAllActive();
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveProducts() {
        when(jpaProductRepository.findAllActive()).thenReturn(List.of());

        List<Product> products = productRepositoryImpl.findAll();

        assertThat(products).isEmpty();
        verify(jpaProductRepository).findAllActive();
    }

    @Test
    void shouldDeactivateProductById() {
        doNothing().when(jpaProductRepository).deactivateById(productId);

        productRepositoryImpl.deactivateById(productId);

        verify(jpaProductRepository).deactivateById(productId);
    }

    @Test
    void shouldCheckIfActiveProductExists() {
        when(jpaProductRepository.existsByIdAndActive(productId)).thenReturn(true);

        boolean exists = productRepositoryImpl.existsByIdAndActive(productId);

        assertThat(exists).isTrue();
        verify(jpaProductRepository).existsByIdAndActive(productId);
    }

    @Test
    void shouldCheckIfActiveProductDoesNotExist() {
        when(jpaProductRepository.existsByIdAndActive(productId)).thenReturn(false);

        boolean exists = productRepositoryImpl.existsByIdAndActive(productId);

        assertThat(exists).isFalse();
        verify(jpaProductRepository).existsByIdAndActive(productId);
    }

    @Test
    void shouldCheckIfProductExists() {
        when(jpaProductRepository.existsById(productId)).thenReturn(true);

        boolean exists = productRepositoryImpl.existsById(productId);

        assertThat(exists).isTrue();
        verify(jpaProductRepository).existsById(productId);
    }

    @Test
    void shouldCheckIfProductDoesNotExist() {
        when(jpaProductRepository.existsById(productId)).thenReturn(false);

        boolean exists = productRepositoryImpl.existsById(productId);

        assertThat(exists).isFalse();
        verify(jpaProductRepository).existsById(productId);
    }

    @Test
    void shouldFindAllProductsWithPagination() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        ProductEntity entity2 = new ProductEntity(
                UUID.randomUUID(),
                "Mouse",
                "Mouse Logitech",
                new BigDecimal("50.00"),
                20
        );
        List<ProductEntity> entities = Arrays.asList(productEntity, entity2);
        Page<ProductEntity> pageResult = new PageImpl<>(entities, pageable, 25L);

        when(jpaProductRepository.findAll(pageable)).thenReturn(pageResult);

        ProductRepository.PageResult<Product> result = productRepositoryImpl.findAll(page, size);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(25L);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.content().get(0).getId()).isEqualTo(productId);
        assertThat(result.content().get(1).getName()).isEqualTo("Mouse");

        verify(jpaProductRepository).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoProductsWithPagination() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> pageResult = new PageImpl<>(List.of(), pageable, 0L);

        when(jpaProductRepository.findAll(pageable)).thenReturn(pageResult);

        ProductRepository.PageResult<Product> result = productRepositoryImpl.findAll(page, size);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();

        verify(jpaProductRepository).findAll(pageable);
    }

    @Test
    void shouldHandleLastPageCorrectly() {
        int page = 2;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;
        Pageable pageable = PageRequest.of(page, size);
        
        // Criar PageImpl com totalElements explícito
        Page<ProductEntity> pageResult = new PageImpl<>(
                List.of(productEntity), 
                pageable, 
                totalElements
        );

        when(jpaProductRepository.findAll(pageable)).thenReturn(pageResult);

        ProductRepository.PageResult<Product> result = productRepositoryImpl.findAll(page, size);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        // Verificar que o totalElements foi passado corretamente
        assertThat(result.totalElements()).isEqualTo(pageResult.getTotalElements());
        assertThat(result.totalPages()).isEqualTo(pageResult.getTotalPages());

        verify(jpaProductRepository).findAll(pageable);
    }
}



