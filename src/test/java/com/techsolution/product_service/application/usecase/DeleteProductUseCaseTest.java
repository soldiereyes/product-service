package com.techsolution.product_service.application.usecase;

import com.techsolution.product_service.domain.ProductRepository;
import com.techsolution.product_service.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;

    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        deleteProductUseCase.execute(productId);

        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.existsById(productId)).thenReturn(false);

        assertThatThrownBy(() -> deleteProductUseCase.execute(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product")
                .hasMessageContaining(productId.toString());

        verify(productRepository).existsById(productId);
        verify(productRepository, never()).deleteById(productId);
    }
}

