package de.uniba.dsg.beverage_store.demo;

import de.uniba.dsg.beverage_store.model.Bottle;
import de.uniba.dsg.beverage_store.model.Crate;
import de.uniba.dsg.beverage_store.model.User;
import de.uniba.dsg.beverage_store.repository.CrateRepository;
import de.uniba.dsg.beverage_store.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Slf4j
@Component
public class DemoData {

    private final UserRepository userRepository;
    private final CrateRepository crateRepository;

    @Autowired
    public DemoData(UserRepository userRepository, CrateRepository crateRepository) {
        this.userRepository = userRepository;
        this.crateRepository = crateRepository;
    }

    @EventListener
    public void createDemoData(ApplicationReadyEvent event) {
        User adminUser = new User(1l, "admin", "$2y$12$uGcYuZ0wt7Kj02tSmsJaBuQsBwdgoL4ZRp1al2fpIboGVHwru6He2", LocalDate.parse("1990-01-01"));

        userRepository.save(adminUser);

        Bottle cocaCola = new Bottle(1l, "Coca-Cola", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 1.0, 0.0, 1.0, "Coca-cola Limited", 1000, null);
        Bottle sprite = new Bottle(2l, "Sprite", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 1.0, 0.0, 1.0, "Coca-cola Limited", 1000, null);
        Bottle pepsi = new Bottle(3l, "Pepsi", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 1.0, 0.0, 1.0, "Pepsi Limited", 1000, null);
        Bottle sevenUp = new Bottle(4l, "7Up", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 1.0, 0.0, 1.0, "Pepsi Limited", 1000, null);

        Crate cocaColaCrate = new Crate(1l, "Coca-Cola Crate", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 10, 10.0, 100, cocaCola);
        Crate spriteCrate = new Crate(2l, "Sprite Crate", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 10, 10.0, 100, sprite);
        Crate pepsiCrate = new Crate(3l, "Pepsi Crate", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 10, 10.0, 100, pepsi);
        Crate sevenUpCrate = new Crate(4l, "7Up Crate", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 10, 10.0, 100, sevenUp);

        crateRepository.saveAll(Arrays.asList(cocaColaCrate, spriteCrate, pepsiCrate, sevenUpCrate));
    }
}
