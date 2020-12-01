package de.uniba.dsg.beverage_store.demo;

import de.uniba.dsg.beverage_store.model.User;
import de.uniba.dsg.beverage_store.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class DemoData {

    private final UserRepository userRepository;

    @Autowired
    public DemoData(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void createDemoData(ApplicationReadyEvent event) {
        User adminUser = new User(0l, "admin", "$2y$12$uGcYuZ0wt7Kj02tSmsJaBuQsBwdgoL4ZRp1al2fpIboGVHwru6He2", LocalDate.parse("1990-01-01"));

        userRepository.save(adminUser);
    }
}
