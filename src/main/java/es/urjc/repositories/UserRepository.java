package es.urjc.repositories;

import es.urjc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring genera: SELECT * FROM user_table WHERE username = ?
    Optional<User> findByUsername(String username);

    // Para comprobar si un email ya está registrado
    boolean existsByEmail(String email);

    // Para comprobar si un username ya está cogido
    boolean existsByUsername(String username);
}