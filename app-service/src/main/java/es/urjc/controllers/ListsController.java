package es.urjc.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.model.User;
import es.urjc.services.RestaurantService;
import es.urjc.services.ListsService;
import es.urjc.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ListsController {
    private static final Logger log = LoggerFactory.getLogger(ListsController.class);
    private final ListsService listsService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    public ListsController(ListsService listsService, UserService userService, RestaurantService restaurantService) {
        this.listsService = listsService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @PostMapping("/lists/create")
    public String createNewList(@RequestParam String name, @RequestParam String description, Principal principal) {
        if (principal == null) return "redirect:/login";

        if (name == null || name.trim().isEmpty()) {
            return "redirect:/profile?error=NombreVacio";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            Lists newList = new Lists(name, description, user);
            listsService.saveList(newList);
        }
        return "redirect:/profile";
    }

    @PostMapping("/lists/delete/{id}")
        public String deleteList(@PathVariable Long id, Principal principal) {
    if (principal == null) return "redirect:/login";

    try {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            listsService.deleteList(id, user);
        }
    } catch (IllegalArgumentException e) {
        log.warn("Unauthorized list deletion attempt: {}", e.getMessage());
    } catch (Exception e) {
        log.error("Error deleting list {}: {}", id, e.getMessage(), e);
    }
    return "redirect:/profile";
    }

    @PostMapping("/lists/{listId}/toggleRestaurant/{restaurantId}")
    public String toggleRestaurantInList(@PathVariable Long listId, @PathVariable Long restaurantId, Principal principal, HttpServletRequest request) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            Lists list = listsService.getListById(listId).orElse(null);
            Restaurant restaurant = restaurantService.findById(restaurantId).orElse(null);

            if (list != null && restaurant != null && list.getOwner().getId().equals(user.getId())) {
                boolean contains = false;
                Restaurant foundRest = null;
                for (Restaurant r : list.getRestaurants()) {
                    if (r.getId().equals(restaurant.getId())) {
                        contains = true;
                        foundRest = r;
                        break;
                    }
                }

                if (contains) {
                    listsService.removeRestaurantFromList(list, foundRest);
                } else {
                    listsService.addRestaurantToList(list, restaurant);
                }
            }
        }
        return "redirect:/restaurants";
    }

    @GetMapping("/lista/{id}")
    public String showListDetails(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("hasAvatar", user.getAvatarImage() != null);
        }

        Lists list = listsService.getListById(id).orElse(null);
        if (list != null) {
            model.addAttribute("list", list);
            model.addAttribute("listRestaurants", list.getRestaurants());
            model.addAttribute("isEmpty", list.getRestaurants().isEmpty());
        }
        return "listdetails";
    }
}