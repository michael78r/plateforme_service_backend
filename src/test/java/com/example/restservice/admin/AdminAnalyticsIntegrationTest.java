package com.example.restservice.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.example.restservice.catalog.Product;
import com.example.restservice.catalog.ProductRepository;
import com.example.restservice.order.Order;
import com.example.restservice.order.OrderItem;
import com.example.restservice.order.OrderRepository;
import com.example.restservice.payment.Payment;
import com.example.restservice.payment.PaymentRepository;
import com.example.restservice.payment.PaymentStatus;
import com.example.restservice.shared.security.JwtUtil;
import com.example.restservice.user.RoleType;
import com.example.restservice.user.Utilisateur;
import com.example.restservice.user.UtilisateurRepository;

/** Tests d'intégration du tableau de bord admin (Phase 2 — bloc 4). */
@SpringBootTest
@AutoConfigureMockMvc
class AdminAnalyticsIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UtilisateurRepository utilisateurRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    JwtUtil jwtUtil;

    private String tokenClient;
    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        utilisateurRepository.deleteAll();

        Utilisateur client = utilisateurRepository.save(user("client@demo.com", RoleType.client));
        tokenClient = token(client);
        tokenAdmin = token(utilisateurRepository.save(user("admin@demo.com", RoleType.admin)));

        Product clavier = productRepository.save(produit("Clavier", new BigDecimal("80.00")));
        Product souris = productRepository.save(produit("Souris", new BigDecimal("40.00")));

        Order order = new Order();
        order.setClient(client);
        order.addItem(new OrderItem(clavier, 2, clavier.getPrice()));
        order.addItem(new OrderItem(souris, 1, souris.getPrice()));
        order.recalculateTotal();
        order = orderRepository.save(order);

        Payment payment = new Payment(order, order.getTotal(), null);
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);
    }

    private Utilisateur user(String email, RoleType role) {
        Utilisateur u = new Utilisateur();
        u.setEmail(email);
        u.setMotDePasse("x");
        u.setRole(role);
        return u;
    }

    private Product produit(String name, BigDecimal price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStockQuantity(10);
        p.setActive(true);
        return p;
    }

    private String token(Utilisateur u) {
        return jwtUtil.generateAccessToken(u.getId().toString(),
                Map.of("role", u.getRole().name(), "email", u.getEmail()));
    }

    @Test
    void un_client_ne_peut_pas_voir_les_stats() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                .header("Authorization", "Bearer " + tokenClient))
                .andExpect(status().isForbidden());
    }

    @Test
    void l_admin_obtient_les_stats_calculees() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(1))
                .andExpect(jsonPath("$.totalProducts").value(2))
                .andExpect(jsonPath("$.totalClients").value(1))
                .andExpect(jsonPath("$.ordersByStatus.PENDING").value(1))
                .andExpect(jsonPath("$.topProducts.length()").value(2))
                .andExpect(jsonPath("$.topProducts[0].name").value("Clavier"))
                .andExpect(jsonPath("$.topProducts[0].quantitySold").value(2));
    }
}
