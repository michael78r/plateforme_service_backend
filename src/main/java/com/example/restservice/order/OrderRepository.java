package com.example.restservice.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientIdOrderByCreatedAtDesc(Long clientId);

    /**
     * Charge une commande en posant un verrou exclusif sur sa ligne, le temps de la transaction.
     * Deux paiements concurrents sur la même commande sont ainsi sérialisés : le second attend,
     * puis constate que la commande n'est plus PENDING → pas de double paiement/facture.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);

    /** Nombre de commandes par statut, sous forme de lignes {@code [OrderStatus, Long]}. */
    @Query("select o.status, count(o) from Order o group by o.status")
    List<Object[]> countGroupedByStatus();
}
