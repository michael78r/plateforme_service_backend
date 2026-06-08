package com.example.restservice.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.restservice.shared.security.JwtUtil;
import com.example.restservice.user.RoleType;
import com.example.restservice.user.Utilisateur;
import com.example.restservice.user.UtilisateurRepository;

/**
 * Tests d'intégration des règles de sécurité de la Phase 1, via de vrais appels HTTP
 * (filtre JWT + chaîne de sécurité réels, base H2 en mémoire).
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderApiSecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UtilisateurRepository utilisateurRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    JwtUtil jwtUtil;

    private Long ownerId;
    private Long orderId;
    private String tokenOwner;
    private String tokenOther;
    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        utilisateurRepository.deleteAll();

        Utilisateur owner = utilisateurRepository.save(user("owner@demo.com", RoleType.client));
        Utilisateur other = utilisateurRepository.save(user("other@demo.com", RoleType.client));
        Utilisateur admin = utilisateurRepository.save(user("admin@demo.com", RoleType.admin));
        ownerId = owner.getId();

        Order order = new Order();
        order.setClient(owner);
        order.setShippingAddress("1 rue des Tests");
        order.setTotal(new BigDecimal("10.00"));
        orderId = orderRepository.save(order).getId();

        tokenOwner = token(owner);
        tokenOther = token(other);
        tokenAdmin = token(admin);
    }

    private Utilisateur user(String email, RoleType role) {
        Utilisateur u = new Utilisateur();
        u.setEmail(email);
        u.setMotDePasse("{noop}x"); // non utilisé : on génère les JWT directement
        u.setRole(role);
        return u;
    }

    private String token(Utilisateur u) {
        return jwtUtil.generateAccessToken(u.getId().toString(),
                Map.of("role", u.getRole().name(), "email", u.getEmail()));
    }

    @Test
    void le_proprietaire_peut_lire_sa_commande() throws Exception {
        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", "Bearer " + tokenOwner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(ownerId));
    }

    @Test
    void un_autre_client_recoit_403_sur_une_commande_qui_n_est_pas_la_sienne() throws Exception {
        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", "Bearer " + tokenOther))
                .andExpect(status().isForbidden());
    }

    @Test
    void l_admin_peut_lire_n_importe_quelle_commande() throws Exception {
        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void sans_token_l_acces_est_refuse() throws Exception {
        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void un_client_ne_peut_pas_creer_un_produit() throws Exception {
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + tokenOwner)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"price\":10.0,\"stockQuantity\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void un_admin_peut_creer_un_produit() throws Exception {
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"price\":10.0,\"stockQuantity\":5}"))
                .andExpect(status().isCreated());
    }
}
