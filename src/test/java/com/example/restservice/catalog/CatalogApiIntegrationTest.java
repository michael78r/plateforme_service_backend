package com.example.restservice.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.restservice.shared.security.JwtUtil;
import com.example.restservice.user.RoleType;
import com.example.restservice.user.Utilisateur;
import com.example.restservice.user.UtilisateurRepository;

/** Tests d'intégration du catalogue : catégories (public/admin) et recherche paginée des produits (Phase 2). */
@SpringBootTest
@AutoConfigureMockMvc
class CatalogApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UtilisateurRepository utilisateurRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    JwtUtil jwtUtil;

    private String tokenClient;
    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        utilisateurRepository.deleteAll();

        tokenClient = token(utilisateurRepository.save(user("client@demo.com", RoleType.client)));
        tokenAdmin = token(utilisateurRepository.save(user("admin@demo.com", RoleType.admin)));

        Category clavardage = categoryRepository.save(new Category("Périphériques", "claviers et souris"));
        productRepository.save(produit("Clavier mécanique", new BigDecimal("80.00"), clavardage));
        productRepository.save(produit("Souris gamer", new BigDecimal("40.00"), clavardage));
        productRepository.save(produit("Écran 27 pouces", new BigDecimal("200.00"), null));
    }

    private Utilisateur user(String email, RoleType role) {
        Utilisateur u = new Utilisateur();
        u.setEmail(email);
        u.setMotDePasse("x");
        u.setRole(role);
        return u;
    }

    private Product produit(String name, BigDecimal price, Category category) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStockQuantity(10);
        p.setActive(true);
        p.setCategory(category);
        return p;
    }

    private String token(Utilisateur u) {
        return jwtUtil.generateAccessToken(u.getId().toString(),
                Map.of("role", u.getRole().name(), "email", u.getEmail()));
    }

    @Test
    void la_liste_des_categories_est_publique() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void un_client_ne_peut_pas_creer_une_categorie() throws Exception {
        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + tokenClient)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Nouvelle\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void un_admin_peut_creer_une_categorie() throws Exception {
        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Audio\",\"description\":\"casques\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Audio"));
    }

    @Test
    void la_recherche_par_mot_cle_filtre_les_produits() throws Exception {
        mockMvc.perform(get("/api/products/search").param("q", "clavier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Clavier mécanique"));
    }

    @Test
    void la_recherche_pagine_les_resultats() throws Exception {
        mockMvc.perform(get("/api/products/search").param("size", "2").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void la_recherche_filtre_par_categorie() throws Exception {
        Long categoryId = categoryRepository.findByName("Périphériques").orElseThrow().getId();
        mockMvc.perform(get("/api/products/search").param("categoryId", categoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
