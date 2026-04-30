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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

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
                // Si el login falla, redirige al formulario desde el que vino
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