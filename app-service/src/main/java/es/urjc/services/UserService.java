package es.urjc.services;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.model.User;
import es.urjc.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- READ ---

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // --- CHECKS ---

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // --- CREATE (Register) ---

    public User registerNewUser(String firstName, String lastName, String email, String username, String rawPassword) {
    if (rawPassword == null || rawPassword.length() < 6) {
        throw new IllegalArgumentException("Password must be at least 6 characters");
    }
    if (email == null || !email.contains("@")) {
        throw new IllegalArgumentException("Invalid email format");
    }
    if (existsByUsername(username)) {
        throw new IllegalArgumentException("Username already taken");
    }
    if (existsByEmail(email)) {
        throw new IllegalArgumentException("Email already registered");
    }

    String encodedPassword = passwordEncoder.encode(rawPassword);
    User newUser = new User(firstName, lastName, email, username, encodedPassword, "", "USER");
    return userRepository.save(newUser);
}

    // --- UPDATE (Edit Profile - básico: nombre, apellido, bio, avatar) ---

    public User updateUser(User user, String firstName, String lastName, String bio, MultipartFile avatarFile) throws Exception {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBio(bio);

        if (avatarFile != null && !avatarFile.isEmpty()) {
            user.setAvatarImage(new SerialBlob(avatarFile.getBytes()));
        }

        return userRepository.save(user);
    }

    // --- UPDATE (Edit Profile - completo: también email, username, password) ---

    public User updateUserFull(User user, String firstName, String lastName, String bio,
                               String email, String username, String password,
                               MultipartFile avatarFile) throws Exception {

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBio(bio);

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }

        if (username != null && !username.isBlank()) {
            user.setUsername(username);
        }

        if (password != null && !password.isBlank()) {
            user.setEncodedPassword(passwordEncoder.encode(password));
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            user.setAvatarImage(new SerialBlob(avatarFile.getBytes()));
        }

        return userRepository.save(user);
    }

    // --- DELETE ---

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // --- AVATAR ---

    public byte[] getAvatarBytes(User user) throws SQLException {
        if (user.getAvatarImage() == null) {
            return null;
        }
        Blob blob = user.getAvatarImage();
        return blob.getBytes(1, (int) blob.length());
    }
}