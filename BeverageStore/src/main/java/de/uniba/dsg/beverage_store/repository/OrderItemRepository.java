package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.db.BeverageOrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<BeverageOrderItem, Long> {
    @EntityGraph(value = "OrderItem.orderItems")
    List<BeverageOrderItem> findAllByOrderOrderNumber(String orderNumber);
}
