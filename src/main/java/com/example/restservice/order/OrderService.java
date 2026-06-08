package com.example.restservice.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restservice.catalog.Product;
import com.example.restservice.catalog.ProductRepository;
import com.example.restservice.catalog.ProductService;
import com.example.restservice.order.dto.OrderItemRequest;
import com.example.restservice.shared.email.EmailService;
import com.example.restservice.shared.exception.BusinessException;
import com.example.restservice.shared.exception.ResourceNotFoundException;
import com.example.restservice.user.Utilisateur;
import com.example.restservice.user.UtilisateurRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final UtilisateurRepository utilisateurRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
            ProductService productService, UtilisateurRepository utilisateurRepository,
            EmailService emailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.utilisateurRepository = utilisateurRepository;
        this.emailService = emailService;
    }

    /** Crée une commande pour un client : valide le stock, fige les prix et décrémente l'inventaire. */
    @Transactional
    public Order createOrder(Long clientId, List<OrderItemRequest> lines, String shippingAddress) {
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException("La commande doit contenir au moins un article");
        }
        Utilisateur client = utilisateurRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + clientId));

        Order order = new Order();
        order.setClient(client);
        order.setShippingAddress(shippingAddress);

        for (OrderItemRequest line : lines) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : " + line.productId()));
            if (!product.isActive()) {
                throw new BusinessException("Produit indisponible : " + product.getName());
            }
            productService.decreaseStock(product, line.quantity());
            order.addItem(new OrderItem(product, line.quantity(), product.getPrice()));
        }

        order.recalculateTotal();
        Order saved = orderRepository.save(order);

        // Notification asynchrone (l'email extrait ici les données pour ne pas toucher d'entité lazy hors transaction)
        emailService.sendOrderConfirmation(client.getEmail(), saved.getId(), saved.getTotal());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> findByClient(Long clientId) {
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId);
    }

    @Transactional(readOnly = true)
    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : " + id));
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = getById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    /** Annule une commande non encore expédiée et réapprovisionne le stock. */
    @Transactional
    public Order cancel(Long id) {
        Order order = getById(id);
        if (order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Impossible d'annuler une commande déjà expédiée/livrée");
        }
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
