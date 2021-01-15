package de.uniba.dsg.beverage_store.spring_boot.security;

import de.uniba.dsg.beverage_store.spring_boot.model.db.Role;
import de.uniba.dsg.beverage_store.spring_boot.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder createEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);

        auth.userDetailsService(userService).passwordEncoder(createEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/beverage/bottle/add", "/beverage/crate/add")
                    .hasRole(Role.MANAGER.name())
                .antMatchers("/cart", "/cart/checkout", "/order/**", "/address/**")
                    .hasRole(Role.CUSTOMER.name())
                .antMatchers("/beverage/bottle", "/beverage/crate")
                    .hasAnyRole(Role.MANAGER.name(), Role.CUSTOMER.name())
                .antMatchers("/", "/home", "/api/**")
                    .authenticated()
                .antMatchers("/scripts/**", "/stylesheets/**")
                    .permitAll()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/home")
                .and()
                .logout()
                    .logoutSuccessUrl("/")
                .and()
                .csrf()
                    .ignoringAntMatchers("/api/**")
                .and()
                .headers()
                    .frameOptions()
                    .sameOrigin()
                .and()
                .logout()
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/login");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login");
    }
}
