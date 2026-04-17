package es.urjc.controllers;

import es.urjc.model.Restaurant;
import es.urjc.services.RestaurantService;
import es.urjc.services.ReviewService;
import es.urjc.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "admin";
    }

    // ── Gestión de restaurantes ──────────────────────────────────────────────
    @GetMapping("/admin/restaurants")
    public String showAdminRestaurants(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        model.addAttribute("restaurants", restaurantService.findAll());
        model.addAttribute("restaurantForm", new Restaurant());
        model.addAttribute("topRestaurants", reviewService.getTop5Restaurants());

        userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
            model.addAttribute("adminName", u.getFirstName());
            model.addAttribute("adminId", u.getId());
            model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
        });

        return "admin-restaurants";
    }

    @PostMapping("/admin/restaurants/new")
    public String createRestaurant(@ModelAttribute("restaurantForm") Restaurant restaurant, Model model,
                                @AuthenticationPrincipal UserDetails currentUser) {
        try {
            restaurantService.save(restaurant);
            return "redirect:/admin/restaurants";
        } catch (Exception e) {
            model.addAttribute("restaurants", restaurantService.findAll());
            model.addAttribute("restaurantForm", restaurant);
            model.addAttribute("topRestaurants", reviewService.getTop5Restaurants());

            userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
                model.addAttribute("adminName", u.getFirstName());
                model.addAttribute("adminId", u.getId());
                model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
                model.addAttribute("adminUser", u);
            });

            model.addAttribute("error", "No se pudo guardar el restaurante");
            return "admin-restaurants";
        }
    }

    @PostMapping("/admin/restaurants/{id}/edit")
    public String updateRestaurant(@PathVariable Long id,
                                   @ModelAttribute("restaurantForm") Restaurant restaurant) {
        restaurantService.update(id, restaurant);
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/admin/restaurants/{id}/delete")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteById(id);
        return "redirect:/admin/restaurants";
    }
}