package de.uniba.dsg.beverage_store.repository;

import de.uniba.dsg.beverage_store.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
}
