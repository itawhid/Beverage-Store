package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.BeverageOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeverageOrderRepository extends JpaRepository<BeverageOrder, Long> {
    @EntityGraph(value = "BeverageOrder.beverageOrders")
    Page<BeverageOrder> findAllByUserUsernameOrderByOrderNumber(String userName, Pageable pageable);

    @EntityGraph(value = "BeverageOrder.beverageOrders")
    Optional<BeverageOrder> findBeverageOrderByOrderNumber(String orderNumber);
}
