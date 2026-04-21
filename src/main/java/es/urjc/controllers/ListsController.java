package es.urjc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpServletRequest;

import es.urjc.model.Lists;
import es.urjc.model.User;
import es.urjc.model.Restaurant;
import es.urjc.services.ListsService;
import es.urjc.services.UserService;
import es.urjc.repositories.RestaurantRepository;

import java.security.Principal;
import java.util.List;

@Controller
public class ListsController {

    private final ListsService listsService;
    private final UserService userService;
    private final RestaurantRepository restaurantRepository;

    public ListsController(ListsService listsService, UserService userService, RestaurantRepository restaurantRepository) {
        this.listsService = listsService;
        this.userService = userService;
        this.restaurantRepository = restaurantRepository;
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
        System.out.println("============================================");
        System.out.println(">>> DELETE LIST ENDPOINT HIT <<< id=" + id);
        System.out.println(">>> Principal: " + (principal == null ? "NULL" : principal.getName()));
        System.out.println("============================================");

        if (principal == null) {
            System.out.println(">>> No principal, redirecting to login");
            return "redirect:/login";
        }

        try {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            System.out.println(">>> User found: " + (user != null ? user.getUsername() + " (id=" + user.getId() + ")" : "NULL"));

            if (user != null) {
                Lists listToDelete = listsService.getListById(id).orElse(null);
                System.out.println(">>> List found: " + (listToDelete != null ? listToDelete.getName() : "NULL"));

                if (listToDelete != null) {
                    System.out.println(">>> List owner id: " + listToDelete.getOwner().getId() + " | User id: " + user.getId());
                    if (listToDelete.getOwner().getId().equals(user.getId())) {
                        System.out.println(">>> Ownership matches, deleting...");
                        listsService.deleteList(id);
                        System.out.println(">>> DELETED OK");
                    } else {
                        System.out.println(">>> Ownership MISMATCH, not deleting");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(">>> EXCEPTION in deleteList:");
            e.printStackTrace();
        }
        return "redirect:/profile";
    }

    @PostMapping("/lists/{listId}/toggleRestaurant/{restaurantId}")
    public String toggleRestaurantInList(@PathVariable Long listId, @PathVariable Long restaurantId, Principal principal, HttpServletRequest request) {
        if (principal == null) return "redirect:/login";

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            Lists list = listsService.getListById(listId).orElse(null);
            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

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