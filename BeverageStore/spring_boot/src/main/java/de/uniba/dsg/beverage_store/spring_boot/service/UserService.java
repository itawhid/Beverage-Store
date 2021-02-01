package de.uniba.dsg.beverage_store.spring_boot.service;

import de.uniba.dsg.beverage_store.spring_boot.exception.NotFoundException;
import de.uniba.dsg.beverage_store.spring_boot.model.db.ApplicationUser;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Role;
import de.uniba.dsg.beverage_store.spring_boot.properties.CustomerProperties;
import de.uniba.dsg.beverage_store.spring_boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final CustomerProperties customerProperties;

    @Autowired
    public UserService(UserRepository userRepository, CustomerProperties customerProperties) {
        this.userRepository = userRepository;
        this.customerProperties = customerProperties;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ApplicationUser> userOptional = userRepository.findByUsername(username);

        if(userOptional.isPresent()) {
            return userOptional.get();
        }

        throw new UsernameNotFoundException("User '" + username + "' not found!");
    }

    public ApplicationUser getUserByUserName(String username) throws NotFoundException {
        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No User found with Username: " + username);
        }

        return optionalUser.get();
    }

    public Page<ApplicationUser> getPagedCustomers(int page) {
        return userRepository.findAllByRole(PageRequest.of(page - 1, customerProperties.getPageSize()), Role.ROLE_CUSTOMER);
    }
}
