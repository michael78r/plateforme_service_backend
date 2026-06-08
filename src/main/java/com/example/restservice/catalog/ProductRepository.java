package com.example.restservice.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByCategoryId(Long categoryId);
    Optional<Product> findBySku(String sku);

    /**
     * Décrémente le stock de façon atomique : la condition {@code stock >= qty} et la mise à jour
     * se font dans la même requête, ce qui empêche la survente en cas de commandes concurrentes.
     *
     * @return le nombre de lignes modifiées (1 = succès, 0 = stock insuffisant ou produit absent)
     */
    @Modifying
    @Query("update Product p set p.stockQuantity = p.stockQuantity - :qty "
            + "where p.id = :id and p.stockQuantity >= :qty")
    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);
}
