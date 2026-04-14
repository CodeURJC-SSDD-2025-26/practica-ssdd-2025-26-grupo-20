package es.urjc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import es.urjc.services.RepositoryUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    // Defines the password encryption algorithm (BCrypt is the standard)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Connects Spring Security with our database user loader
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        return new DaoAuthenticationProvider(userDetailsService);
    }
    
    // Main security rules: who can access what
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth

                // Public pages — anyone can access
                .requestMatchers("/", "/restaurants", "/restaurant/**").permitAll()
                .requestMatchers("/login", "/signup", "/logout").permitAll()
                .requestMatchers("/assets/**", "/vendor/**","/css/**", "/js/**", "/images/**", "/static/**").permitAll()

                // Admin only pages
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Logged in users only
                .requestMatchers("/profile/**", "/user/**").hasAnyRole("USER", "ADMIN")

                // Everything else requires login
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")           // Our custom login page
                .defaultSuccessUrl("/", true)  // Redirect here after login
                .failureUrl("/login?error")    // Redirect here if login fails
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")  // Redirect here after logout
                .permitAll()
            );

        return http.build();
    }
}