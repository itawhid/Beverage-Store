package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.db.BeverageOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<BeverageOrder, Long> {
    @EntityGraph(value = "Order.orders")
    Page<BeverageOrder> findAllByUserUsernameOrderByOrderNumber(String userName, Pageable pageable);

    @EntityGraph(value = "Order.orders")
    Optional<BeverageOrder> findOrderByOrderNumber(String orderNumber);
}
