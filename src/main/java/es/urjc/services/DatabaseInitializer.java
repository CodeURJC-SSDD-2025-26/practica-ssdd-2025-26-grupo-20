package es.urjc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.urjc.model.User;
import es.urjc.repositories.UserRepository;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Only initialize if the database is empty
        if (userRepository.count() == 0) {

            // Create admin user
            User admin = new User(
                "Admin1",
                "administrador",
                "admin1@celisud.com",
                "admin",
                passwordEncoder.encode("admin"),
                "Soy el admin 1 de la aplicación",
                "USER", "ADMIN"
            );

            // Create example user 1
            User user1 = new User(
                "John",
                "Doe",
                "john@celisud.com",
                "johndoe",
                passwordEncoder.encode("1234"),
                "I love gluten-free restaurants!",
                "USER"
            );

            // Create example user 2
            User user2 = new User(
                "Jane",
                "Smith",
                "jane@celisud.com",
                "janesmith",
                passwordEncoder.encode("1234"),
                "Always looking for new places to eat",
                "USER"
            );

            userRepository.save(admin);
            userRepository.save(user1);
            userRepository.save(user2);

            System.out.println("✅ Database initialized with example users");
        } else {
            System.out.println("✅ Database already has data, skipping initialization");
        }
    }
}