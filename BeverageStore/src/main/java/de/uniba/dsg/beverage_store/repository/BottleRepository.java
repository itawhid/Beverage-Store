package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.Bottle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BottleRepository extends JpaRepository<Bottle, Long> {
    Page<Bottle> findByOrderByNameAsc(Pageable pageable);
}
