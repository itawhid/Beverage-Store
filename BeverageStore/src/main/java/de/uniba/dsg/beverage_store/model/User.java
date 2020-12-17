package de.uniba.dsg.beverage_store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uniba.dsg.beverage_store.validation.annotation.LaterThanOrEqualTo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Username is required.")
    @NotEmpty(message = "Username cannot be empty.")
    private String username;

    @NotNull(message = "First Name is required.")
    @NotEmpty(message = "First Name cannot be empty.")
    private String firstName;

    @NotNull(message = "Last Name is required.")
    @NotEmpty(message = "Last Name cannot be empty.")
    private String lastName;

    @NotNull(message = "Password is required.")
    @NotEmpty(message = "Password cannot be empty.")
    private String password;

    @NotNull
    @LaterThanOrEqualTo(year = "1990", month = "01", dayOfMonth = "01")
    private LocalDate birthday;



    // Entity Relations
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Address> addresses;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> orders;

    // Authentication
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
