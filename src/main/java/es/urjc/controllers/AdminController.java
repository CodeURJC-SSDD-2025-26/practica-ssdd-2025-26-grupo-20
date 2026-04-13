package es.urjc.controllers;

import es.urjc.model.Restaurant;
import es.urjc.services.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminController {

    private final RestaurantService restaurantService;

    public AdminController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String showAdminRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantService.findAll());
        model.addAttribute("restaurantForm", new Restaurant());
        return "admin-restaurants";
    }

    @PostMapping("/new")
    public String createRestaurant(@ModelAttribute("restaurantForm") Restaurant restaurant) {
        restaurantService.save(restaurant);
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/{id}/edit")
    public String updateRestaurant(@PathVariable Long id,
                                   @ModelAttribute("restaurantForm") Restaurant restaurant) {
        restaurantService.update(id, restaurant);
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/{id}/delete")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteById(id);
        return "redirect:/admin/restaurants";
    }
}