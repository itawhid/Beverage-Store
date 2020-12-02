package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByUsername(String username);
}
