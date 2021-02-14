package de.uniba.dsg.beverage_store.spring_boot;

import de.uniba.dsg.beverage_store.spring_boot.demo.DemoData;
import de.uniba.dsg.beverage_store.spring_boot.model.BeverageType;
import de.uniba.dsg.beverage_store.spring_boot.model.db.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

public class TestHelper {
    public static Crate getCrate() {
        return DemoData.crates.stream()
                .findFirst()
                .orElse(null);
    }

    public static Bottle getBottle() {
        return DemoData.bottles.stream()
                .findFirst()
                .orElse(null);
    }

    public static Address getAddress() {
        return DemoData.addresses.stream()
                .findFirst()
                .orElse(null);
    }

    public static ApplicationUser getUser() {
        return DemoData.applicationUsers.stream()
                .findFirst()
                .orElse(null);
    }

    public static ApplicationUser getManager() {
        return DemoData.applicationUsers.stream()
                .filter(x -> x.getRole() == Role.ROLE_MANAGER)
                .findFirst()
                .orElse(null);
    }

    public static ApplicationUser getCustomer() {
        return DemoData.applicationUsers.stream()
                .filter(x -> x.getRole() == Role.ROLE_CUSTOMER)
                .findFirst()
                .orElse(null);
    }

    public static Address getUserAddress(String username) {
        return DemoData.addresses.stream()
                .filter(x -> x.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public static BeverageOrder getMockOrder() {
        Address address = new Address(null, "Address 1", "Pestalozzistraße", "9F", "96052", null, null, null);

        ApplicationUser customer = new ApplicationUser(null, "testuser1", "Test", "User1", "testuser1@email.com", null, LocalDate.of(1990, 1, 1), Role.ROLE_CUSTOMER, null, null);

        return new BeverageOrder(null, "Order01", LocalDate.now(), 20.0, customer, address, address, null);
    }

    public static List<Address> getMockAddresses() {
        Address address1 = new Address(null, "Address 1", "Pestalozzistraße", "9F", "96052", null, null, null);
        Address address2 = new Address(null, "Address 2", "Kapellenstraße", "23", "96050", null, null, null);

        return Arrays.asList(address1, address2);
    }

    public static ApplicationUser getMockCustomer() {
        return new ApplicationUser(null, "testuser1", "Test", "User1", "testuser1@email.com", null, LocalDate.of(1990, 1, 1), Role.ROLE_CUSTOMER, null, null);
    }

    public static List<ApplicationUser> getMockCustomers() {
        ApplicationUser customer1 = new ApplicationUser(null, "testuser1", "Test", "User1", "testuser1@email.com", null, LocalDate.of(1990, 1, 1), Role.ROLE_CUSTOMER, null, null);
        ApplicationUser customer2 = new ApplicationUser(null, "testuser2", "Test", "User2", "testuser2@email.com", null, LocalDate.of(1990, 1, 1), Role.ROLE_CUSTOMER, null, null);

        return Arrays.asList(customer1, customer2);
    }

    public static List<BeverageOrderItem> getMockOrderItems() {
        Bottle bottle1 = new Bottle(null, "Pepsi", "Pepsi pic", 1.0, 0.0, 1.0, "Pepsi Limited", 10, null, null);
        Bottle bottle2 = new Bottle(null, "Coca-cola", "Coca-cola pic", 1.0, 0.0, 1.0, "Coca-cola Limited", 10, null, null);

        BeverageOrderItem orderItem1 = new BeverageOrderItem(null, BeverageType.BOTTLE, 10, 1, bottle1, null, null);
        BeverageOrderItem orderItem2 = new BeverageOrderItem(null, BeverageType.BOTTLE, 10, 2, bottle2, null, null);

        return Arrays.asList(orderItem1, orderItem2);
    }

    public static List<BeverageOrder> getMockOrdersForManager() {
        ApplicationUser customer1 = new ApplicationUser(null, "testuser1", "Test", "User1", "testuser1@email.com", null, LocalDate.of(1990, 1, 1), Role.ROLE_CUSTOMER, null, null);
        ApplicationUser customer2 = new ApplicationUser(null, "testuser2", "Test", "User2", "testuser2@email.com", null, LocalDate.of(1990, 1, 1), Role.ROLE_CUSTOMER, null, null);

        BeverageOrder order1 = new BeverageOrder(null, "Order01", LocalDate.now(), 10.0, customer1, null, null, null);
        BeverageOrder order2 = new BeverageOrder(null, "Order02", LocalDate.now(), 20.0, customer1, null, null, null);
        BeverageOrder order3 = new BeverageOrder(null, "Order03", LocalDate.now(), 30.0, customer2, null, null, null);

        return Arrays.asList(order1, order2, order3);
    }

    public static List<BeverageOrder> getMockOrdersForCustomer() {
        ApplicationUser customer = new ApplicationUser(null, "testuser1", "Test", "User1", "testuser1@email.com", null, LocalDate.of(1990, 1, 1), Role.ROLE_CUSTOMER, null, null);

        BeverageOrder order1 = new BeverageOrder(null, "Order01", LocalDate.now(), 10.0, customer, null, null, null);
        BeverageOrder order2 = new BeverageOrder(null, "Order02", LocalDate.now(), 20.0, customer, null, null, null);

        return Arrays.asList(order1, order2);
    }

    public static MultiValueMap<String, String> getPageParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "1");

        return params;
    }

    public static MockHttpServletRequestBuilder createGetRequest(String url, UserDetails user, MultiValueMap<String, String> params) {
        return MockMvcRequestBuilders.get(url)
                .with(user == null ? anonymous() : user(user))
                .params(params);
    }

    public static MockHttpServletRequestBuilder createPostRequest(String url, UserDetails user, MultiValueMap<String, String> params) {
        return MockMvcRequestBuilders.post(url)
                .with(csrf())
                .with(user == null ? anonymous() : user(user))
                .params(params);
    }
}
