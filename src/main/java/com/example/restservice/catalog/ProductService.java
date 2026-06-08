package com.example.restservice.catalog;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restservice.shared.exception.BusinessException;
import com.example.restservice.shared.exception.ResourceNotFoundException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> findActive() {
        return productRepository.findByActiveTrue();
    }

    /** Recherche paginée des produits actifs (mot-clé et catégorie optionnels). */
    @Transactional(readOnly = true)
    public Page<Product> search(String q, Long categoryId, Pageable pageable) {
        // Jamais null : "" => like '%%' => aucun filtre sur le nom (et évite lower(bytea) sur PostgreSQL).
        String keyword = (q == null) ? "" : q.trim();
        return productRepository.search(keyword, categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : " + id));
    }

    @Transactional
    public Product create(Product product, Long categoryId) {
        if (categoryId != null) {
            product.setCategory(loadCategory(categoryId));
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product details, Long categoryId) {
        Product product = getById(id);
        product.setName(details.getName());
        product.setDescription(details.getDescription());
        product.setPrice(details.getPrice());
        product.setStockQuantity(details.getStockQuantity());
        product.setSku(details.getSku());
        product.setImageUrl(details.getImageUrl());
        product.setActive(details.isActive());
        product.setCategory(categoryId != null ? loadCategory(categoryId) : null);
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit introuvable : " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Décrémente le stock de façon atomique (anti-survente sous concurrence).
     * Le contrôle de disponibilité et la mise à jour sont faits en une seule requête SQL.
     */
    @Transactional
    public void decreaseStock(Product product, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("La quantité doit être positive");
        }
        int updated = productRepository.decreaseStock(product.getId(), quantity);
        if (updated == 0) {
            throw new BusinessException("Stock insuffisant pour le produit : " + product.getName());
        }
    }

    private Category loadCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable : " + categoryId));
    }
}
