package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.OrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(value = "OrderItem.orderItems")
    List<OrderItem> findAllByOrderOrderNumber(String orderNumber);
}
