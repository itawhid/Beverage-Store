package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.BeverageOrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeverageOrderItemRepository extends JpaRepository<BeverageOrderItem, Long> {
    @EntityGraph(value = "BeverageOrderItem.beverageOrderItems")
    List<BeverageOrderItem> findAllByBeverageOrderOrderNumber(String orderNumber);
}
