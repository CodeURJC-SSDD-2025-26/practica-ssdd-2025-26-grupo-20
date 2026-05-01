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
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import es.urjc.services.RepositoryUserDetailsService;

// =========================================================
// ALEXIS
// =========================================================
// Este fichero necesita dos modificaciones principales:
//
// MODIFICACIÓN 1 — Añadir un segundo SecurityFilterChain para la API REST
//   Crea un nuevo método anotado con @Bean y @Order(1) ANTES del método
//   securityFilterChain que ya existe (que pasará a ser @Order(2)).
//   El nuevo método debe:
//     - Usar securityMatcher("/api/v1/**") para que solo aplique a la API
//     - Deshabilitar CSRF (rúbrica #26)
//     - Configurar sesión como STATELESS (rúbrica #27)
//     - Definir qué URLs son públicas y cuáles requieren rol (rúbrica #14)
//     - Añadir tu JwtFilter antes de UsernamePasswordAuthenticationFilter
//     - Configurar un AuthenticationEntryPoint que devuelva JSON 401
//       en vez de redirigir al login de la web (rúbrica #18)
//
// MODIFICACIÓN 2 — Añadir el método securityFilterChain existente con @Order(2)
//   Simplemente añade @Order(2) encima de @Bean en el método que ya existe.
//   NO cambies nada más de ese método — está funcionando correctamente.
//
// IMPORTS que necesitarás añadir:
//   import org.springframework.context.annotation.Order;
//   import org.springframework.security.config.http.SessionCreationPolicy;
//   import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//   import es.urjc.security.JwtFilter;
// =========================================================

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    // TODO (Persona A): añade aquí @Autowired de JwtFilter cuando lo hayas creado

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // TODO (Persona A): añade aquí el nuevo @Bean @Order(1) para la API REST
    // según las instrucciones del comentario de arriba

    @Order(2) 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf
            .ignoringRequestMatchers("/admin/**")
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/restaurants", "/restaurants/**", "/restaurant/**").permitAll()
                .requestMatchers("/login", "/loginuser", "/loginadmin", "/signup", "/logout", "/lists/**").permitAll()
                .requestMatchers("/templatemo_580_woox_travel/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/assets/**").permitAll()
                .requestMatchers("/restaurants/*/image", "/restaurant/*/image").permitAll()
                .requestMatchers("/user/{id}/avatar").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/profile/**", "/profileadmin/**", "/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/process-login")
                .successHandler((request, response, authentication) -> {
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    if (isAdmin) {
                        response.sendRedirect("/admin");
                    } else {
                        response.sendRedirect("/");
                    }
                })
                .failureHandler((request, response, exception) -> {
                    String referer = request.getHeader("Referer");
                    if (referer != null) {
                        if (referer.contains("/loginadmin")) {
                            response.sendRedirect("/loginadmin?error");
                        } else if (referer.contains("/loginuser") || referer.contains("/signup")) {
                            response.sendRedirect("/loginuser?error");
                        } else {
                            response.sendRedirect("/login?error");
                        }
                    } else {
                        response.sendRedirect("/login?error");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<MultipartFilter> multipartFilter() {
        FilterRegistrationBean<MultipartFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new MultipartFilter());
        filterBean.setOrder(0);
        return filterBean;
    }
}