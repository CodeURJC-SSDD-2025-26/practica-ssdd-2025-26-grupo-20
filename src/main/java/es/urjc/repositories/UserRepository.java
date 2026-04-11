package es.urjc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.model.User;
import java.util.*;


public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
}
