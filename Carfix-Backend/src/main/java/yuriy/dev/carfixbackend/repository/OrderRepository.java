package yuriy.dev.carfixbackend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yuriy.dev.carfixbackend.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {"works","car","user"})
    List<Order> findAll();

    @Query("select o.orderDate from Order o where o.status = 'ACCEPTED' or o.status = 'PENDING'")
    List<LocalDateTime> findOrderDates();
}
