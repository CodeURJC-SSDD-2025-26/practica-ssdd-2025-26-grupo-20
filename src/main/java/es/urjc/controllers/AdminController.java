package es.urjc.controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import java.util.Optional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.urjc.model.Restaurant;
import es.urjc.services.RestaurantService;
import es.urjc.services.ReviewService;
import es.urjc.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ReviewService reviewService;

    // ── Panel principal (Dashboard) ──────────────────────────────────────────
    @GetMapping("/admin")
    public String adminPanel(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
            model.addAttribute("adminName", u.getFirstName());
            model.addAttribute("adminId", u.getId());
            model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
            model.addAttribute("adminUser", u);
        });
        return "admin/admin";
    }

    // ── Gestión de restaurantes ──────────────────────────────────────────────
    @GetMapping("/admin/restaurants")
    public String showAdminRestaurants(@AuthenticationPrincipal UserDetails currentUser,
                                       Model model,
                                       HttpServletRequest request) {
        model.addAttribute("restaurants", restaurantService.findAll());
        model.addAttribute("restaurantForm", new Restaurant());
        model.addAttribute("topRestaurants", reviewService.getTop5Restaurants());

        userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
            model.addAttribute("adminName", u.getFirstName());
            model.addAttribute("adminId", u.getId());
            model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
        });

        // Inyectar token CSRF para Mustache
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            model.addAttribute("_csrf_token", csrf.getToken());
        }

        return "admin-restaurants";
    }

    @PostMapping("/admin/restaurants/new")
    public String createRestaurant(@ModelAttribute("restaurantForm") Restaurant restaurant,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            @AuthenticationPrincipal UserDetails currentUser,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            System.out.println("ERRORES DE BINDING: " + bindingResult.getAllErrors());
            model.addAttribute("restaurants", restaurantService.findAll());
            model.addAttribute("restaurantForm", restaurant);
            model.addAttribute("topRestaurants", reviewService.getTop5Restaurants());
            userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
                model.addAttribute("adminName", u.getFirstName());
                model.addAttribute("adminId", u.getId());
                model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
                model.addAttribute("adminUser", u);
            });
            model.addAttribute("error", "Revisa los campos del formulario");
            return "admin-restaurants";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    Blob blob = new javax.sql.rowset.serial.SerialBlob(imageFile.getBytes());
                    restaurant.setImageFile(blob);
                } catch (Exception ex) {
                    // si falla la imagen, continúa sin ella
                }
            }
            restaurantService.save(restaurant);
            return "redirect:/admin/restaurants";

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR CREAR: " + e.getMessage());
            System.out.println("CAUSA: " + e.getCause());
            model.addAttribute("restaurants", restaurantService.findAll());
            model.addAttribute("restaurantForm", restaurant);
            model.addAttribute("topRestaurants", reviewService.getTop5Restaurants());
            userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
                model.addAttribute("adminName", u.getFirstName());
                model.addAttribute("adminId", u.getId());
                model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
                model.addAttribute("adminUser", u);
            });
            CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrf != null) {
                model.addAttribute("_csrf_token", csrf.getToken());
            }
            model.addAttribute("error", "No se pudo guardar el restaurante: " + e.getMessage());
            return "admin-restaurants";
        }
    }

    @PostMapping("/admin/restaurants/{id}/edit")
    public String updateRestaurant(
            @PathVariable Long id,
            @ModelAttribute("restaurantForm") Restaurant restaurant,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Blob blob = new javax.sql.rowset.serial.SerialBlob(imageFile.getBytes());
                restaurant.setImageFile(blob);
            }
        } catch (Exception e) {
            // continúa sin imagen
        }
        restaurantService.update(id, restaurant);
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/admin/restaurants/{id}/delete")
    public String deleteRestaurant(@PathVariable Long id, 
                                RedirectAttributes redirectAttributes) {
        try {
            restaurantService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace(); // <-- AÑADE ESTO
            System.out.println("ERROR REAL: " + e.getMessage()); // <-- Y ESTO
            System.out.println("CAUSA: " + e.getCause()); // <-- Y ESTO
            redirectAttributes.addFlashAttribute("error", 
                "No se pudo eliminar el restaurante. Puede tener reseñas o listas asociadas.");
        }
        return "redirect:/admin/restaurants";
    }
    @InitBinder("restaurantForm")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("imageFile");
    }
    @GetMapping("/restaurant/{id}/image")
    public void getRestaurantImage(@PathVariable Long id, HttpServletResponse response) {
        try {
            Optional<Restaurant> opt = restaurantService.findById(id);
            if (opt.isEmpty() || opt.get().getImageFile() == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Restaurant restaurant = opt.get();
            Blob blob = restaurant.getImageFile();
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            response.setContentType("image/jpeg");
            response.getOutputStream().write(bytes);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/admin/restaurants/{id}/deleteImage")
    public String deleteRestaurantImage(@PathVariable Long id) {
        restaurantService.findById(id).ifPresent(r -> {
            r.setImageFile(null);
            restaurantService.save(r);
        });
        return "redirect:/admin/restaurants";
    }
}