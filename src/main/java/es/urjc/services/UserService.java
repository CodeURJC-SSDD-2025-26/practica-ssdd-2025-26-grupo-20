package es.urjc.services;

import org.springframework.stereotype.Service;
import es.urjc.model.User;
import es.urjc.repositories.UserRepository;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Este es el método que tu ListsController estaba buscando y salía en rojo
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}