package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
