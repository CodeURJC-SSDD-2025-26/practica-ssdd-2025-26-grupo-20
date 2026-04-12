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

    @GetMapping("/profile")
    public String showProfileAndLists(Model model, Principal principal) {
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("isAuthenticated", true);
            List<Lists> userLists = listsService.getListsByUser(user);
            model.addAttribute("myLists", userLists);
        }
        return "profile"; 
    }

    @PostMapping("/lists/create")
    public String createNewList(@RequestParam String name, @RequestParam String description, Principal principal) {
        if (name == null || name.trim().isEmpty()) {
            return "redirect:/profile?error=NombreVacio"; 
        }
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        if (user != null) {
            Lists newList = new Lists(name, description, user);
            listsService.saveList(newList);
        }
        return "redirect:/profile";
    }

    @PostMapping("/lists/delete/{id}")
    public String deleteList(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        if (user != null) {
            Lists listToDelete = listsService.getListById(id).orElse(null);
            if (listToDelete != null && listToDelete.getOwner().getId().equals(user.getId())) {
                listsService.deleteList(id);
            }
        }
        return "redirect:/profile";
    }

    @PostMapping("/lists/{listId}/toggleRestaurant/{restaurantId}")
    public String toggleRestaurantInList(@PathVariable Long listId, @PathVariable Long restaurantId, Principal principal, HttpServletRequest request) {
        User user = userService.findByUsername("chicote_gluten").orElse(null);
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
        User user = userService.findByUsername("chicote_gluten").orElse(null);
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