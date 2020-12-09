package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.Bottle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BottleRepository extends JpaRepository<Bottle, Long> {
    List<Bottle> findByOrderByNameAsc();
}
