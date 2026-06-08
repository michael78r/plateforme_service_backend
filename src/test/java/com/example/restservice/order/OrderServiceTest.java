package com.example.restservice.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.restservice.catalog.Product;
import com.example.restservice.catalog.ProductRepository;
import com.example.restservice.catalog.ProductService;
import com.example.restservice.order.dto.OrderItemRequest;
import com.example.restservice.shared.exception.BusinessException;
import com.example.restservice.user.Utilisateur;
import com.example.restservice.user.UtilisateurRepository;

/** Tests unitaires des règles de commande (création, total, annulation). */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductService productService;
    @Mock
    UtilisateurRepository utilisateurRepository;
    @InjectMocks
    OrderService orderService;

    private Utilisateur client(Long id) {
        Utilisateur u = new Utilisateur();
        u.setId(id);
        return u;
    }

    private Product produit(Long id, boolean actif, String prix) {
        Product p = new Product();
        p.setId(id);
        p.setName("Produit " + id);
        p.setActive(actif);
        p.setPrice(new BigDecimal(prix));
        return p;
    }

    @Test
    void createOrder_rejette_une_commande_sans_ligne() {
        assertThatThrownBy(() -> orderService.createOrder(1L, List.of(), "adresse"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void createOrder_rejette_un_produit_inactif() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client(1L)));
        when(productRepository.findById(10L)).thenReturn(Optional.of(produit(10L, false, "20.00")));

        assertThatThrownBy(() -> orderService.createOrder(1L,
                List.of(new OrderItemRequest(10L, 1)), "adresse"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("indisponible");
    }

    @Test
    void createOrder_calcule_le_total_et_decremente_le_stock() {
        Product p = produit(10L, true, "20.00");
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client(1L)));
        when(productRepository.findById(10L)).thenReturn(Optional.of(p));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order order = orderService.createOrder(1L,
                List.of(new OrderItemRequest(10L, 2)), "adresse");

        verify(productService).decreaseStock(p, 2);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotal()).isEqualByComparingTo("40.00");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void cancel_interdit_l_annulation_d_une_commande_expediee() {
        Order order = new Order();
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancel(5L))
                .isInstanceOf(BusinessException.class);
    }
}
