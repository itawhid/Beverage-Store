package de.uniba.dsg.beverage_store.spring_boot.repository;

import de.uniba.dsg.beverage_store.spring_boot.model.db.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findUserByUsername(String username);
}
