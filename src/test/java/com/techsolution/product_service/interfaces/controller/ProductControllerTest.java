package com.techsolution.product_service.interfaces.controller;

import com.techsolution.product_service.application.usecase.CreateProductUseCase;
import com.techsolution.product_service.application.usecase.DeleteProductUseCase;
import com.techsolution.product_service.application.usecase.GetProductByIdUseCase;
import com.techsolution.product_service.application.usecase.ListProductsUseCase;
import com.techsolution.product_service.application.usecase.UpdateProductUseCase;
import com.techsolution.product_service.interfaces.dto.CreateProductRequest;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import com.techsolution.product_service.interfaces.dto.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private UpdateProductUseCase updateProductUseCase;

    @Mock
    private GetProductByIdUseCase getProductByIdUseCase;

    @Mock
    private ListProductsUseCase listProductsUseCase;

    @Mock
    private DeleteProductUseCase deleteProductUseCase;

    @InjectMocks
    private ProductController productController;

    private UUID productId;
    private CreateProductRequest createRequest;
    private UpdateProductRequest updateRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        createRequest = new CreateProductRequest(
                "Notebook",
                "Notebook Dell Inspiron 15",
                new BigDecimal("3500.00"),
                10
        );
        updateRequest = new UpdateProductRequest(
                "Notebook Updated",
                "Updated Description",
                new BigDecimal("3800.00"),
                15
        );
        productResponse = new ProductResponse(
                productId,
                "Notebook",
                "Notebook Dell Inspiron 15",
                new BigDecimal("3500.00"),
                10
        );
    }

    @Test
    void shouldCreateProduct() {
        when(createProductUseCase.execute(createRequest)).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.create(createRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(productResponse);
        verify(createProductUseCase).execute(createRequest);
    }

    @Test
    void shouldUpdateProduct() {
        when(updateProductUseCase.execute(productId, updateRequest)).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.update(productId, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(productResponse);
        verify(updateProductUseCase).execute(productId, updateRequest);
    }

    @Test
    void shouldGetProductById() {
        when(getProductByIdUseCase.execute(productId)).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getById(productId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(productResponse);
        verify(getProductByIdUseCase).execute(productId);
    }

    @Test
    void shouldListProducts() {
        List<ProductResponse> products = Arrays.asList(productResponse);
        when(listProductsUseCase.execute()).thenReturn(products);

        ResponseEntity<List<ProductResponse>> response = productController.list();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(products);
        verify(listProductsUseCase).execute();
    }

    @Test
    void shouldDeleteProduct() {
        doNothing().when(deleteProductUseCase).execute(productId);

        ResponseEntity<Void> response = productController.delete(productId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(deleteProductUseCase).execute(productId);
    }
}



