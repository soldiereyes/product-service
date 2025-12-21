package com.techsolution.product_service.interfaces.controller;

import com.techsolution.product_service.application.usecase.CreateProductUseCase;
import com.techsolution.product_service.application.usecase.DeleteProductUseCase;
import com.techsolution.product_service.application.usecase.GetProductByIdUseCase;
import com.techsolution.product_service.application.usecase.ListProductsUseCase;
import com.techsolution.product_service.application.usecase.UpdateProductUseCase;
import com.techsolution.product_service.interfaces.dto.CreateProductRequest;
import com.techsolution.product_service.interfaces.dto.PageResponse;
import com.techsolution.product_service.interfaces.dto.ProductResponse;
import com.techsolution.product_service.interfaces.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            ListProductsUseCase listProductsUseCase,
            DeleteProductUseCase deleteProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        logger.info("Creating product with name: {}", request.name());
        ProductResponse response = createProductUseCase.execute(request);
        logger.info("Product created successfully with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        logger.info("Updating product with id: {}", id);
        ProductResponse response = updateProductUseCase.execute(id, request);
        logger.info("Product updated successfully with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        logger.info("Getting product by id: {}", id);
        ProductResponse response = getProductByIdUseCase.execute(id);
        logger.info("Product found with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        logger.info("Listing products - page: {}, size: {}", page, size);
        
        // Validação de parâmetros
        if (page < 0) {
            return ResponseEntity.badRequest().body("Page must be greater than or equal to 0");
        }
        if (size <= 0 || size > 100) {
            return ResponseEntity.badRequest().body("Size must be between 1 and 100");
        }
        
        PageResponse<ProductResponse> response = listProductsUseCase.execute(page, size);
        logger.info("Found {} products (page {} of {})", 
                response.content().size(), page, response.totalPages());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        logger.info("Deleting product with id: {}", id);
        deleteProductUseCase.execute(id);
        logger.info("Product deleted successfully with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}



