package com.example.restservice.order;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.restservice.admin.dto.TopProduct;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** Produits les plus vendus (quantités cumulées), du plus vendu au moins vendu. */
    @Query("select new com.example.restservice.admin.dto.TopProduct("
            + "oi.product.id, oi.product.name, sum(oi.quantity)) "
            + "from OrderItem oi "
            + "group by oi.product.id, oi.product.name "
            + "order by sum(oi.quantity) desc")
    List<TopProduct> findTopProducts(Pageable pageable);
}
