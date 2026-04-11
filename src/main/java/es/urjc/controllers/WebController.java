package es.urjc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

import es.urjc.repositories.RestaurantRepository;
import es.urjc.services.UserService;
import es.urjc.model.User;

@Controller
public class WebController {

    private final RestaurantRepository restaurantRepository;
    private final UserService userService;

    public WebController(RestaurantRepository restaurantRepository, UserService userService) {
        this.restaurantRepository = restaurantRepository;
        this.userService = userService;
    }

    @GetMapping("/")
    public String showIndex(Model model, Principal principal) {
        model.addAttribute("restaurants", restaurantRepository.findAll());
        
        // HACK TEMPORAL: Forzamos que siempre haya un usuario logueado (Chicote) para que puedas probar tus Listas
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        if (user != null) {
            model.addAttribute("user", user); 
        }
        
        return "index";
    }

    @GetMapping("/restaurants")
    public String showRestaurants(Model model, Principal principal) {
        model.addAttribute("restaurants", restaurantRepository.findAll());
        
        // HACK TEMPORAL
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
        }
        
        return "restaurants";
    }
}