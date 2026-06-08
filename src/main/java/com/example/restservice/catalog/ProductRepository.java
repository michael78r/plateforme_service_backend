package com.example.restservice.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** Nombre de produits dont le stock est strictement sous un seuil (alerte réapprovisionnement). */
    long countByStockQuantityLessThan(int threshold);

    /**
     * Recherche paginée des produits actifs, avec filtres optionnels :
     * un mot-clé sur le nom ({@code q}, chaîne vide = pas de filtre) et/ou une catégorie ({@code categoryId}, {@code null} = pas de filtre).
     *
     * <p>{@code q} ne doit jamais être {@code null} : un bind {@code null} non typé est interprété
     * comme {@code bytea} par PostgreSQL et fait échouer {@code lower(...)}. Le service passe "" par défaut,
     * ce qui donne {@code like '%%'} (toutes les lignes).
     */
    @Query("select p from Product p where p.active = true "
            + "and lower(p.name) like lower(concat('%', :q, '%')) "
            + "and (:categoryId is null or p.category.id = :categoryId)")
    Page<Product> search(@Param("q") String q, @Param("categoryId") Long categoryId, Pageable pageable);

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
