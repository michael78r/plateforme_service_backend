package com.example.restservice.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.restservice.shared.exception.BusinessException;

/** Tests unitaires de la logique de stock (Phase 1.3 — anti-survente). */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    CategoryRepository categoryRepository;
    @InjectMocks
    ProductService productService;

    private Product produit(Long id) {
        Product p = new Product();
        p.setId(id);
        p.setName("Produit " + id);
        return p;
    }

    @Test
    void decreaseStock_rejette_une_quantite_non_positive() {
        Product p = produit(1L);

        assertThatThrownBy(() -> productService.decreaseStock(p, 0))
                .isInstanceOf(BusinessException.class);

        // La requête de décrément ne doit même pas être tentée.
        verifyNoInteractions(productRepository);
    }

    @Test
    void decreaseStock_rejette_si_le_decrement_atomique_echoue() {
        Product p = produit(1L);
        // 0 ligne modifiée = stock insuffisant côté base (condition WHERE stock >= qty non remplie)
        when(productRepository.decreaseStock(1L, 5)).thenReturn(0);

        assertThatThrownBy(() -> productService.decreaseStock(p, 5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuffisant");
    }

    @Test
    void decreaseStock_reussit_quand_une_ligne_est_modifiee() {
        Product p = produit(1L);
        when(productRepository.decreaseStock(1L, 2)).thenReturn(1);

        productService.decreaseStock(p, 2);

        verify(productRepository).decreaseStock(1L, 2);
    }

    @Test
    void decreaseStock_utilise_bien_un_decrement_atomique_et_pas_un_save() {
        Product p = produit(1L);
        when(productRepository.decreaseStock(1L, 3)).thenReturn(1);

        productService.decreaseStock(p, 3);

        // L'ancienne implémentation faisait un save() : on garantit qu'on ne revient pas en arrière.
        assertThat(p.getStockQuantity()).isZero();
    }
}
