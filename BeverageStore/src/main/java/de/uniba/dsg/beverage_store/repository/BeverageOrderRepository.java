package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.BeverageOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeverageOrderRepository extends JpaRepository<BeverageOrder, Long> {
    @EntityGraph(value = "BeverageOrder.beverageOrders")
    List<BeverageOrder> findAllByUserUsernameOrderByOrderNumber(String userName);

    @EntityGraph(value = "BeverageOrder.beverageOrders")
    Optional<BeverageOrder> findBeverageOrderByOrderNumber(String orderNumber);
}
