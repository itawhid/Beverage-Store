package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserUsername(String userName);
}
