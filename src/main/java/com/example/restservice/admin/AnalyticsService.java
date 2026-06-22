package com.example.restservice.admin;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restservice.admin.dto.DashboardStats;
import com.example.restservice.admin.dto.TopProduct;
import com.example.restservice.catalog.ProductRepository;
import com.example.restservice.order.OrderItemRepository;
import com.example.restservice.order.OrderRepository;
import com.example.restservice.order.OrderStatus;
import com.example.restservice.payment.PaymentRepository;
import com.example.restservice.payment.PaymentStatus;
import com.example.restservice.user.RoleType;
import com.example.restservice.user.UtilisateurRepository;

@Service
public class AnalyticsService {

    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final int TOP_PRODUCTS_LIMIT = 5;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AnalyticsService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository, ProductRepository productRepository,
            UtilisateurRepository utilisateurRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStats getDashboard() {
        List<TopProduct> topProducts = orderItemRepository
                .findTopProducts(PageRequest.of(0, TOP_PRODUCTS_LIMIT));

        return new DashboardStats(
                paymentRepository.sumAmountByStatus(PaymentStatus.PAID),
                orderRepository.count(),
                ordersByStatus(),
                productRepository.count(),
                productRepository.countByStockQuantityLessThan(LOW_STOCK_THRESHOLD),
                utilisateurRepository.countByRole(RoleType.client),
                topProducts);
    }

    private Map<OrderStatus, Long> ordersByStatus() {
        Map<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0L);
        }
        for (Object[] row : orderRepository.countGroupedByStatus()) {
            counts.put((OrderStatus) row[0], (Long) row[1]);
        }
        return counts;
    }
}
