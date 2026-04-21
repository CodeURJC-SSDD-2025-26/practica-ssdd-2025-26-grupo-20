package es.urjc.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.model.User;
import es.urjc.services.EmailService;
import es.urjc.services.RestaurantService;
import es.urjc.services.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private EmailService emailService;

    // -------------------------------------------------------
    // SIGNUP
    // -------------------------------------------------------

    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "signup";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "signup";
        }
        if (!email.contains("@")) {
            model.addAttribute("error", "Formato de email inválido");
            return "signup";
        }
        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Nombre de usuario ya registrado");
            return "signup";
        }
        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email ya registrado");
            return "signup";
        }

        try {
            userService.registerNewUser(firstName, lastName, email, username, password);
            emailService.sendWelcomeEmail(email, firstName); // correo de bienvenida
            return "redirect:/loginuser?registered";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error, por favor inténtalo de nuevo");
            return "signup";
        }
    }

    // -------------------------------------------------------
    // PROFILE
    // -------------------------------------------------------

    @GetMapping("/profile")
    public String showMyProfile(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(value = "updated", required = false) String updated,
            Model model) {

        Optional<User> optUser = userService.findByUsername(currentUser.getUsername());
        if (optUser.isEmpty()) return "redirect:/login";

        User user = optUser.get();
        model.addAttribute("user", user);
        model.addAttribute("hasAvatar", user.getAvatarImage() != null);
        model.addAttribute("updated", updated != null);
        model.addAttribute("myLists", user.getFavoriteLists());
        model.addAttribute("myReviews", user.getReviews());
        model.addAttribute("hasReviews", !user.getReviews().isEmpty());
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String processEditProfile(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String bio,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) MultipartFile avatarFile,
            Model model) {

        Optional<User> optUser = userService.findByUsername(currentUser.getUsername());
        if (optUser.isEmpty()) return "redirect:/login";

        try {
            userService.updateUserFull(optUser.get(), firstName, lastName, bio, email, username, password, avatarFile);
            return "redirect:/profile?updated";
        } catch (Exception e) {
            User user = optUser.get();
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("hasAvatar", user.getAvatarImage() != null);
            model.addAttribute("myLists", user.getFavoriteLists());
            model.addAttribute("myReviews", user.getReviews());
            return "profile";
        }
    }

    // -------------------------------------------------------
    // PROFILE ADMIN
    // -------------------------------------------------------

    @GetMapping("/profileadmin")
    public String showMyProfileAdmin(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(value = "updated", required = false) String updated,
            Model model) {

        Optional<User> optUser = userService.findByUsername(currentUser.getUsername());
        if (optUser.isEmpty()) return "redirect:/loginadmin";

        User user = optUser.get();
        model.addAttribute("user", user);
        model.addAttribute("hasAvatar", user.getAvatarImage() != null);
        model.addAttribute("adminName", user.getFirstName());
        model.addAttribute("adminId", user.getId());
        model.addAttribute("updated", updated != null);
        model.addAttribute("totalUsers", userService.findAll().size());
        model.addAttribute("totalRestaurants", restaurantService.findAll().size());
        return "profileadmin";
    }

    @PostMapping("/profileadmin/edit")
    public String processEditProfileAdmin(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String bio,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) MultipartFile avatarFile,
            Model model) {

        Optional<User> optUser = userService.findByUsername(currentUser.getUsername());
        if (optUser.isEmpty()) return "redirect:/login";

        try {
            userService.updateUserFull(optUser.get(), firstName, lastName, bio, email, username, password, avatarFile);
            return "redirect:/profileadmin?updated";
        } catch (Exception e) {
            User user = optUser.get();
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("hasAvatar", user.getAvatarImage() != null);
            model.addAttribute("adminName", user.getFirstName());
            model.addAttribute("adminId", user.getId());
            return "profileadmin";
        }
    }

    // -------------------------------------------------------
    // AVATAR IMAGE
    // -------------------------------------------------------

    @GetMapping("/user/{id}/avatar")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable Long id) {

        Optional<User> optUser = userService.findById(id);

        if (optUser.isEmpty() || optUser.get().getAvatarImage() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageBytes = userService.getAvatarBytes(optUser.get());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // -------------------------------------------------------
    // DELETE ACCOUNT
    // -------------------------------------------------------

    @PostMapping("/profile/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails currentUser) {
        Optional<User> optUser = userService.findByUsername(currentUser.getUsername());
        optUser.ifPresent(user -> userService.deleteUser(user.getId()));
        return "redirect:/logout";
    }

    // -------------------------------------------------------
    // ADMIN - list all users
    // -------------------------------------------------------

    @GetMapping("/admin/users")
    public String listAllUsers(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
            model.addAttribute("adminName", u.getFirstName());
            model.addAttribute("adminId", u.getId());
            model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
        });

        return "admin/user-list";
    }

    @GetMapping("/admin/users/{id}")
    public String viewUserProfile(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser, Model model) {

        Optional<User> optUser = userService.findById(id);

        if (optUser.isEmpty()) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", optUser.get());
        model.addAttribute("hasAvatar", optUser.get().getAvatarImage() != null);

        userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
            model.addAttribute("adminName", u.getFirstName());
            model.addAttribute("adminId", u.getId());
            model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
        });

        return "admin/user-profile";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String adminDeleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}