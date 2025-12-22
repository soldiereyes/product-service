package com.techsolution.product_service.infrastructure.persistence.jpa;

import com.techsolution.product_service.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
    
    @Query("SELECT p FROM ProductEntity p WHERE p.active = true")
    List<ProductEntity> findAllActive();
    
    @Query("SELECT p FROM ProductEntity p WHERE p.active = true")
    Page<ProductEntity> findAllActive(Pageable pageable);
    
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id AND p.active = true")
    Optional<ProductEntity> findByIdAndActive(@Param("id") UUID id);
    
    @Query("SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.id = :id AND p.active = true")
    boolean existsByIdAndActive(@Param("id") UUID id);
    
    @Modifying
    @Query("UPDATE ProductEntity p SET p.active = false WHERE p.id = :id")
    void deactivateById(@Param("id") UUID id);
}


