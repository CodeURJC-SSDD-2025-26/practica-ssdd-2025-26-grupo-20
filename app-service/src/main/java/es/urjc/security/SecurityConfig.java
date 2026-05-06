package es.urjc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.multipart.support.MultipartFilter;

import es.urjc.services.RepositoryUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

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

    // -------------------------------------------------------
    // CADENA 1: API REST — sin CSRF, sin sesión, con JWT
    // -------------------------------------------------------
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/v1/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Avatares públicos
                .requestMatchers("/api/v1/users/*/avatar").permitAll()
                // Imágenes de restaurantes públicas
                .requestMatchers("/api/v1/restaurants/*/image").permitAll()
                // Lectura pública de restaurantes
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/restaurants/**").permitAll()
                // Lista de usuarios solo para ADMIN
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                // El resto requiere autenticación
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + authException.getMessage() + "\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"" + accessDeniedException.getMessage() + "\"}");
                })
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // -------------------------------------------------------
    // CADENA 2: Web tradicional — con CSRF, con sesión, con login form
    // -------------------------------------------------------
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(org.springframework.security.config.Customizer.withDefaults()
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/restaurants", "/restaurants/**", "/restaurant/**").permitAll()
                .requestMatchers("/login", "/loginuser", "/loginadmin", "/signup", "/logout", "/lists/**", "/error").permitAll()
                .requestMatchers("/templatemo_580_woox_travel/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/assets/**", "/vendor/**").permitAll()
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