package es.urjc.controllers.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.dto.UserDTO;
import es.urjc.model.User;
import es.urjc.security.JwtUtils;
import es.urjc.services.RepositoryUserDetailsService;
import es.urjc.services.UserService;
import es.urjc.services.UtilityClient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    @Autowired private UserService userService;
    @Autowired private RepositoryUserDetailsService userDetailsService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private UtilityClient utilityClient;

    // --------------------------------------------------
    // Request bodies (inner records)
    // --------------------------------------------------

    public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
    ) {}

    public record SignupRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password
    ) {}

    // --------------------------------------------------
    // POST /api/v1/auth/login
    // --------------------------------------------------

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(req.username());
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!passwordEncoder.matches(req.password(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        String token = jwtUtils.generateToken(userDetails.getUsername(), roles);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // --------------------------------------------------
    // POST /api/v1/auth/signup
    // --------------------------------------------------

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        User user = userService.registerNewUser(
                req.firstName(), req.lastName(), req.email(), req.username(), req.password());

        utilityClient.sendWelcomeEmail(req.email(), req.firstName());

        UserDTO dto = toDto(user);
        URI location = URI.create("/api/v1/users/" + user.getId());
        return ResponseEntity.created(location).body(dto);
    }

    // --------------------------------------------------
    // Mapper
    // --------------------------------------------------

    private UserDTO toDto(User u) {
        return new UserDTO(u.getId(), u.getUsername(), u.getFirstName(),
                u.getLastName(), u.getEmail(), u.getBio(), u.getRoles());
    }
}