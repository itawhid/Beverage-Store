package de.uniba.dsg.beverage_store.demo;

import de.uniba.dsg.beverage_store.helper.Helper;
import de.uniba.dsg.beverage_store.model.*;
import de.uniba.dsg.beverage_store.repository.*;
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
    private final AddressRepository addressRepository;
    private final BeverageOrderRepository beverageOrderRepository;
    private final BeverageOrderItemRepository beverageOrderItemRepository;

    @Autowired
    public DemoData(UserRepository userRepository, CrateRepository crateRepository, AddressRepository addressRepository, BeverageOrderRepository beverageOrderRepository, BeverageOrderItemRepository beverageOrderItemRepository) {
        this.userRepository = userRepository;
        this.crateRepository = crateRepository;
        this.addressRepository = addressRepository;
        this.beverageOrderRepository = beverageOrderRepository;
        this.beverageOrderItemRepository = beverageOrderItemRepository;
    }

    @EventListener
    public void createDemoData(ApplicationReadyEvent event) {
        User adminUser = new User(1L, "admin", "Admin", "User", "$2y$12$uGcYuZ0wt7Kj02tSmsJaBuQsBwdgoL4ZRp1al2fpIboGVHwru6He2" /* password actual value: admin */, LocalDate.of(1990, 1, 1), null, null);
        userRepository.save(adminUser);

        Address address1 = new Address(1L, "Address 1", "Pestalozzistraße", "9f", "96052", adminUser, null, null);
        Address address2 = new Address(2L, "Address 2", "Kapellenstraße", "23", "96050", adminUser, null, null);
        addressRepository.saveAll(Arrays.asList(address1, address2));

        Bottle cocaCola = new Bottle(1L, "Coca-Cola", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 1.0, 0.0, 1.0, "Coca-cola Limited", 5, null, null, 0);
        Bottle sprite = new Bottle(2L, "Sprite", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 1.0, 0.0, 1.0, "Coca-cola Limited", 5, null, null, 0);
        Bottle pepsi = new Bottle(3L, "Pepsi", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 1.0, 0.0, 1.0, "Pepsi Limited", 5, null, null, 0);
        Bottle sevenUp = new Bottle(4L, "7Up", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 1.0, 0.0, 1.0, "Pepsi Limited", 5, null, null, 0);

        Crate cocaColaCrate = new Crate(1L, "Coca-Cola Crate", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 10, 10.0, 5, cocaCola, null, 0);
        Crate spriteCrate = new Crate(2L, "Sprite Crate", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 10, 10.0, 5, sprite, null, 0);
        Crate pepsiCrate = new Crate(3L, "Pepsi Crate", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 10, 10.0, 5, pepsi, null, 0);
        Crate sevenUpCrate = new Crate(4L, "7Up Crate", "https://www.google.com/logos/doodles/2020/december-holidays-day-1-6753651837108829.4-law.gif", 10, 10.0, 5, sevenUp, null, 0);

        crateRepository.saveAll(Arrays.asList(cocaColaCrate, spriteCrate, pepsiCrate, sevenUpCrate));

        BeverageOrder beverageOrder = new BeverageOrder(1L, Helper.generateOrderNumber(1L), LocalDate.now(), 12.0, adminUser, address1, address2, null);

        BeverageOrderItem beverageOrderItem1 = new BeverageOrderItem(1L, BeverageType.BOTTLE, 2, cocaCola, null, beverageOrder);
        BeverageOrderItem beverageOrderItem2 = new BeverageOrderItem(2L, BeverageType.CRATE, 1, null, pepsiCrate, beverageOrder);
        beverageOrderItemRepository.saveAll(Arrays.asList(beverageOrderItem1, beverageOrderItem2));
    }
}
