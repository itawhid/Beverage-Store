package de.uniba.dsg.beverage_store.spring_boot.repository;

import de.uniba.dsg.beverage_store.spring_boot.model.db.ApplicationUser;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Page<ApplicationUser> findAllByRole(Pageable page, Role role);

    Optional<ApplicationUser> findByUsername(String username);
}
