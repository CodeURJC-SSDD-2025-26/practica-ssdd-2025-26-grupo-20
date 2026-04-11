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

    // ==========================================
    // 1. MOSTRAR LAS LISTAS EN EL PERFIL
    // ==========================================
    @GetMapping("/profile")
    public String showProfileAndLists(Model model, Principal principal) {
        // HACK TEMPORAL: Forzamos a Chicote para poder probar sin login real
        User user = userService.findByUsername("chicote_gluten").orElse(null);

        if (user != null) {
            model.addAttribute("user", user);
            List<Lists> userLists = listsService.getListsByUser(user);
            model.addAttribute("myLists", userLists);
        }
        
        return "profile"; 
    }

    // ==========================================
    // 2. CREAR UNA LISTA NUEVA
    // ==========================================
    @PostMapping("/lists/create")
    public String createNewList(@RequestParam String name, @RequestParam String description, Principal principal) {
        if (name == null || name.trim().isEmpty()) {
            return "redirect:/profile?error=NombreVacio"; 
        }

        // HACK TEMPORAL: Forzamos a Chicote
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        
        if (user != null) {
            Lists newList = new Lists(name, description, user);
            listsService.saveList(newList);
        }
        
        return "redirect:/profile";
    }

    // ==========================================
    // 3. BORRAR UNA LISTA
    // ==========================================
    @PostMapping("/lists/delete/{id}")
    public String deleteList(@PathVariable Long id, Principal principal) {
        // HACK TEMPORAL: Forzamos a Chicote
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        
        if (user != null) {
            Lists listToDelete = listsService.getListById(id).orElse(null);
            
            // Comprobamos que la lista existe y pertenece al usuario
            if (listToDelete != null && listToDelete.getOwner().getId().equals(user.getId())) {
                listsService.deleteList(id);
            }
        }
        return "redirect:/profile";
    }

    // ==========================================
    // 4. METER O SACAR UN RESTAURANTE (Estilo Spotify)
    // ==========================================
    @PostMapping("/lists/{listId}/toggleRestaurant/{restaurantId}")
    public String toggleRestaurantInList(@PathVariable Long listId, @PathVariable Long restaurantId, Principal principal, HttpServletRequest request) {
        // HACK TEMPORAL: Forzamos a Chicote
        User user = userService.findByUsername("chicote_gluten").orElse(null);

        if (user != null) {
            Lists list = listsService.getListById(listId).orElse(null);
            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null); 
            
            if (list != null && restaurant != null && list.getOwner().getId().equals(user.getId())) {
                // Si el restaurante ya está en la lista, lo quitamos. Si no está, lo añadimos.
                if (list.getRestaurants().contains(restaurant)) {
                    listsService.removeRestaurantFromList(list, restaurant);
                } else {
                    listsService.addRestaurantToList(list, restaurant);
                }
            }
        }
        
        // Te devuelve a la página exacta en la que hiciste clic (portada, catálogo o detalles)
        String referer = request.getHeader("Referer");
        if (referer != null) {
            return "redirect:" + referer;
        }
        return "redirect:/restaurants";
    }
}