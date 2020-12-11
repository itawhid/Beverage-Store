package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.User;
import de.uniba.dsg.beverage_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByUsername(username);

        if(userOptional.isPresent()) {
            return userOptional.get();
        }

        throw new UsernameNotFoundException("User '" + username + "' not found!");
    }

    public User getUserByUserName(String username) throws NotFoundException {
        Optional<User> optionalUser = userRepository.findUserByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No User found with Username: " + username);
        }

        return optionalUser.get();
    }
}
