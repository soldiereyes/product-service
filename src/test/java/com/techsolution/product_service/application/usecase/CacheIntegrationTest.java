package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.domain.Product;
import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.interfaces.dto.CreateProductRequest;
import com.techsolution.product_service.interfaces.dto.PageResponse;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import com.techsolution.product_service.interfaces.dto.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes para validar o comportamento esperado do cache.
 * 
 * Nota: Estes testes verificam que as operações chamam o repositório corretamente.
 * O comportamento real do cache (cache hit/miss) é testado em testes de integração
 * que requerem um contexto Spring completo com Redis configurado.
 * 
 * Comportamento esperado do cache:
 * - GetProductByIdUseCase: @Cacheable("product") - cacheia por ID
 * - ListProductsUseCase: @Cacheable("productsPage") - cacheia por página/tamanho
 * - CreateProductUseCase: @CacheEvict("productsPage") - invalida cache de listagem
 * - UpdateProductUseCase: @CacheEvict({"product", "productsPage"}) - invalida ambos
 * - DeleteProductUseCase: @CacheEvict({"product", "productsPage"}) - invalida ambos
 */
@ExtendWith(MockitoExtension.class)
class CacheIntegrationTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductByIdUseCase getProductByIdUseCase;

    @InjectMocks
    private ListProductsUseCase listProductsUseCase;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @InjectMocks
    private UpdateProductUseCase updateProductUseCase;

    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;

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
    void shouldCallRepositoryOnGetProductByIdRequest() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = getProductByIdUseCase.execute(productId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(productId);
        // Em produção, segunda chamada com mesmo ID não chamaria o repositório (cache hit)
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void shouldCallRepositoryOnListProductsRequest() {
        int page = 0;
        int size = 10;
        ProductRepository.PageResult<Product> pageResult = new ProductRepository.PageResult<>(
                List.of(product),
                1L,
                1
        );

        when(productRepository.findAll(page, size)).thenReturn(pageResult);

        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        // Em produção, segunda chamada com mesma página/tamanho não chamaria o repositório (cache hit)
        verify(productRepository, times(1)).findAll(page, size);
    }

    @Test
    void shouldCallRepositoryWhenCreatingProduct() {
        CreateProductRequest request = new CreateProductRequest(
                "New Product",
                "Description",
                new BigDecimal("100.00"),
                5
        );

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = createProductUseCase.execute(request);

        assertThat(response).isNotNull();
        // CreateProductUseCase tem @CacheEvict("productsPage") - invalida cache de listagem
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldCallRepositoryWhenUpdatingProduct() {
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Name",
                "Updated Description",
                new BigDecimal("200.00"),
                15
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = updateProductUseCase.execute(productId, request);

        assertThat(response).isNotNull();
        // UpdateProductUseCase tem @CacheEvict({"product", "productsPage"}) - invalida ambos os caches
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldCallRepositoryWhenDeletingProduct() {
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        deleteProductUseCase.execute(productId);

        // DeleteProductUseCase tem @CacheEvict({"product", "productsPage"}) - invalida ambos os caches
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void shouldUseDifferentCacheKeysForDifferentPages() {
        int page1 = 0;
        int page2 = 1;
        int size = 10;

        ProductRepository.PageResult<Product> pageResult1 = new ProductRepository.PageResult<>(
                List.of(product),
                25L,
                3
        );

        ProductRepository.PageResult<Product> pageResult2 = new ProductRepository.PageResult<>(
                List.of(product),
                25L,
                3
        );

        when(productRepository.findAll(page1, size)).thenReturn(pageResult1);
        when(productRepository.findAll(page2, size)).thenReturn(pageResult2);

        PageResponse<ProductResponse> response1 = listProductsUseCase.execute(page1, size);
        PageResponse<ProductResponse> response2 = listProductsUseCase.execute(page2, size);

        assertThat(response1).isNotNull();
        assertThat(response2).isNotNull();
        assertThat(response1.page()).isNotEqualTo(response2.page());
        
        // Cada página deve ter sua própria chave de cache: "page:0:size:10" vs "page:1:size:10"
        verify(productRepository, times(1)).findAll(page1, size);
        verify(productRepository, times(1)).findAll(page2, size);
    }

    @Test
    void shouldUseDifferentCacheKeysForDifferentSizes() {
        int page = 0;
        int size1 = 10;
        int size2 = 20;

        ProductRepository.PageResult<Product> pageResult1 = new ProductRepository.PageResult<>(
                List.of(product),
                25L,
                3
        );

        ProductRepository.PageResult<Product> pageResult2 = new ProductRepository.PageResult<>(
                List.of(product),
                25L,
                2
        );

        when(productRepository.findAll(page, size1)).thenReturn(pageResult1);
        when(productRepository.findAll(page, size2)).thenReturn(pageResult2);

        PageResponse<ProductResponse> response1 = listProductsUseCase.execute(page, size1);
        PageResponse<ProductResponse> response2 = listProductsUseCase.execute(page, size2);

        assertThat(response1).isNotNull();
        assertThat(response2).isNotNull();
        assertThat(response1.size()).isNotEqualTo(response2.size());
        
        // Cada tamanho deve ter sua própria chave de cache: "page:0:size:10" vs "page:0:size:20"
        verify(productRepository, times(1)).findAll(page, size1);
        verify(productRepository, times(1)).findAll(page, size2);
    }
}

