package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.Crate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrateRepository extends JpaRepository<Crate, Long> {
    @Override
    @EntityGraph(value = "Crate.crates")
    Optional<Crate> findById(Long aLong);

    @EntityGraph(value = "Crate.crates")
    Page<Crate> findByOrderByNameAsc(Pageable pageable);
}
