package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.Bottle;
import org.springframework.data.repository.CrudRepository;

public interface BottleRepository extends CrudRepository<Bottle, Long> {
}
