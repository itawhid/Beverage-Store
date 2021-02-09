package de.uniba.dsg.beverage_store.spring_boot.unit_test;

import de.uniba.dsg.beverage_store.spring_boot.demo.DemoData;
import de.uniba.dsg.beverage_store.spring_boot.exception.CredentialConflictException;
import de.uniba.dsg.beverage_store.spring_boot.exception.NotFoundException;
import de.uniba.dsg.beverage_store.spring_boot.model.db.ApplicationUser;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Role;
import de.uniba.dsg.beverage_store.spring_boot.model.dto.CustomerDTO;
import de.uniba.dsg.beverage_store.spring_boot.repository.UserRepository;
import de.uniba.dsg.beverage_store.spring_boot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void loadUserByUsername_test() {
        ApplicationUser expectedUser = getUser();

        assertNotNull(expectedUser);

        UserDetails actualUser = userService.loadUserByUsername(expectedUser.getUsername());

        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getAuthorities(), actualUser.getAuthorities());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("Test User"));
    }

    @Test
    public void getUserByUserName_test() throws NotFoundException {
        ApplicationUser expectedUser = getUser();

        assertNotNull(expectedUser);

        ApplicationUser actualUser = userService.getUserByUserName(expectedUser.getUsername());

        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getBirthday(), actualUser.getBirthday());
        assertEquals(expectedUser.getRole(), actualUser.getRole());
        assertEquals(expectedUser.getAuthorities(), actualUser.getAuthorities());

        assertThrows(NotFoundException.class, () -> userService.getUserByUserName("Test User"));
    }

    @Test
    @Transactional
    public void addCustomer_test() throws CredentialConflictException {
        long countBeforeAdd = userRepository.findAllByRole(Role.ROLE_CUSTOMER)
                .size();

        CustomerDTO customerDTO = new CustomerDTO(
                "Test",
                "User",
                "test-user",
                "testuser@beveragestore.com",
                "test-user",
                "test-user",
                LocalDate.of(1993, 01, 01));

        ApplicationUser addedCustomer = userService.addCustomer(customerDTO);

        assertNotNull(addedCustomer);
        assertNotNull(addedCustomer.getId());
        assertEquals(customerDTO.getFirstName(), customerDTO.getFirstName());
        assertEquals(customerDTO.getLastName(), customerDTO.getLastName());
        assertEquals(customerDTO.getUsername(), customerDTO.getUsername());
        assertEquals(customerDTO.getEmail(), customerDTO.getEmail());
        assertEquals(customerDTO.getBirthday(), customerDTO.getBirthday());
        assertEquals(countBeforeAdd + 1, userRepository.findAllByRole(Role.ROLE_CUSTOMER).stream().count());

        assertThrows(CredentialConflictException.class, () -> userService.addCustomer(new CustomerDTO(
                "Test",
                "User 2",
                addedCustomer.getUsername(),
                "testuser2@beveragestore.com",
                "test-user",
                "test-user",
                LocalDate.of(1993, 01, 01))));

        assertThrows(CredentialConflictException.class, () -> userService.addCustomer(new CustomerDTO(
                "Test",
                "User",
                "test-user2",
                addedCustomer.getEmail(),
                "test-user",
                "test-user",
                LocalDate.of(1993, 01, 01))));

        assertThrows(CredentialConflictException.class, () -> userService.addCustomer(new CustomerDTO(
                "Test",
                "User 2",
                addedCustomer.getUsername(),
                addedCustomer.getEmail(),
                "test-user",
                "test-user",
                LocalDate.of(1993, 01, 01))));
    }

    private ApplicationUser getUser() {
        return DemoData.applicationUsers.stream()
                .findFirst()
                .orElse(null);
    }
}
