package es.urjc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import es.urjc.model.Lists;
import es.urjc.model.User;
import es.urjc.services.ListsService;
import es.urjc.services.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class ListsController {

    private final ListsService listsService;
    private final UserService userService; // Lo necesitamos para saber quién está logueado

    public ListsController(ListsService listsService, UserService userService) {
        this.listsService = listsService;
        this.userService = userService;
    }

    // 1. MOSTRAR LAS LISTAS EN EL PERFIL
    // Cuando el usuario entra a /profile, interceptamos la ruta para inyectar sus listas
    @GetMapping("/profile")
    public String showProfileAndLists(Model model, Principal principal) {
        // ¿Hay alguien logueado?
        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByUsername(username).orElse(null);

            if (user != null) {
                // Pasamos los datos del usuario a la vista
                model.addAttribute("user", user);

                // Buscamos sus listas y se las pasamos a la vista
                List<Lists> userLists = listsService.getListsByUser(user);
                model.addAttribute("myLists", userLists);
            }
        }
        return "profile"; // Esto busca tu archivo profile.mustache
    }

    // 2. CREAR UNA LISTA NUEVA
    @PostMapping("/lists/create")
    public String createNewList(@RequestParam String name, @RequestParam String description, Principal principal) {
        
        // Validación básica: que el nombre no esté vacío
        if (name == null || name.trim().isEmpty()) {
            return "redirect:/profile?error=NombreVacio"; 
        }

        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            
            if (user != null) {
                // Creamos la lista y la guardamos
                Lists newList = new Lists(name, description, user);
                listsService.saveList(newList);
            }
        }
        // Redirigimos al perfil para que vea su nueva lista creada
        return "redirect:/profile";
    }

    // 3. BORRAR UNA LISTA
    @PostMapping("/lists/delete/{id}")
    public String deleteList(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            Lists listToDelete = listsService.getListById(id).orElse(null);
            
            // SEGURIDAD: Comprobamos que la lista existe Y que el dueño es el usuario que intenta borrarla (Punto 12 de la rúbrica)
            if (listToDelete != null && listToDelete.getOwner().getUsername().equals(principal.getName())) {
                listsService.deleteList(id);
            }
        }
        return "redirect:/profile";
    }
}