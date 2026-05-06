package es.urjc.controllers.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.dto.UserDTO;
import es.urjc.model.User;
import es.urjc.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    // --------------------------------------------------
    // Request body
    // --------------------------------------------------

    public record UpdateUserRequest(
        String firstName,
        String lastName,
        String bio,
        @Email String email,
        @Size(min = 3, max = 50) String username,
        @Size(min = 6) String password
    ) {}

    // --------------------------------------------------
    // GET /api/v1/users  (solo ADMIN — protegido en SecurityConfig)
    // --------------------------------------------------

    @GetMapping
    public ResponseEntity<Page<UserDTO>> listUsers(Pageable pageable) {
        List<User> all = userService.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<UserDTO> dtos = all.subList(start, end).stream()
                .map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(new PageImpl<>(dtos, pageable, all.size()));
    }

    // --------------------------------------------------
    // GET /api/v1/users/{id}
    // --------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    // --------------------------------------------------
    // PUT /api/v1/users/{id}
    // --------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest req,
            @AuthenticationPrincipal UserDetails currentUser) {

        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        if (!isOwnerOrAdmin(currentUser, opt.get())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\":\"No tienes permiso para editar este usuario\"}");
        }

        try {
            User updated = userService.updateUserFull(
                    opt.get(),
                    req.firstName(), req.lastName(), req.bio(),
                    req.email(), req.username(), req.password(),
                    null);
            return ResponseEntity.ok(toDto(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // --------------------------------------------------
    // DELETE /api/v1/users/{id}
    // --------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {

        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        if (!isOwnerOrAdmin(currentUser, opt.get())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\":\"No tienes permiso para borrar este usuario\"}");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------
    // GET /api/v1/users/{id}/avatar
    // --------------------------------------------------

    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty() || opt.get().getAvatarImage() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] bytes = userService.getAvatarBytes(opt.get());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --------------------------------------------------
    // Helpers
    // --------------------------------------------------

    private boolean isOwnerOrAdmin(UserDetails currentUser, User target) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin || currentUser.getUsername().equals(target.getUsername());
    }

    private UserDTO toDto(User u) {
        return new UserDTO(u.getId(), u.getUsername(), u.getFirstName(),
                u.getLastName(), u.getEmail(), u.getBio(), u.getRoles());
    }
}