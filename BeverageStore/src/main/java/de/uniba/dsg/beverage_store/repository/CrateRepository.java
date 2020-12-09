package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.Crate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrateRepository extends JpaRepository<Crate, Long> {
}
