package es.urjc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // Seguimos en modo obras
        )
        .formLogin(form -> form
                .loginPage("/login") // ¡MAGIA! Aquí le decimos que use tu HTML
                .defaultSuccessUrl("/", true) // A dónde ir si aciertas la contraseña
                .permitAll()
        )
        .logout(logout -> logout
                .logoutSuccessUrl("/") // A dónde ir al cerrar sesión
                .permitAll()
        );

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}