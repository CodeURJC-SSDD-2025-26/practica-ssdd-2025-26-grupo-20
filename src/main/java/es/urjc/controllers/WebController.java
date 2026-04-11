package es.urjc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.*;

import es.urjc.repositories.RestaurantRepository;
import es.urjc.services.UserService;
import es.urjc.services.ListsService;
import es.urjc.model.User;
import es.urjc.model.Restaurant;
import es.urjc.model.Lists;

@Controller
public class WebController {

    private final RestaurantRepository restaurantRepository;
    private final UserService userService;
    private final ListsService listsService;

    public WebController(RestaurantRepository restaurantRepository, UserService userService, ListsService listsService) {
        this.restaurantRepository = restaurantRepository;
        this.userService = userService;
        this.listsService = listsService;
    }

    @GetMapping("/")
    public String showIndex(Model model, Principal principal) {
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("isAuthenticated", true);
        }
        model.addAttribute("restaurants", getRestaurantsWithListStatus(user, null));
        return "index";
    }

    @GetMapping("/restaurants")
    public String showRestaurants(@RequestParam(required = false) String q, Model model, Principal principal) {
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("isAuthenticated", true);
        }
        model.addAttribute("restaurants", getRestaurantsWithListStatus(user, q));
        model.addAttribute("searchQuery", q); 
        return "restaurants";
    }

    @GetMapping("/restaurant/{id}")
    public String showRestaurantDetails(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByUsername("chicote_gluten").orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("isAuthenticated", true);
        }
        
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        if (restaurant != null) {
            
            // 1. Lógica del restaurante actual
            boolean isSavedInAnyList = false;
            List<Map<String, Object>> listsStatus = new ArrayList<>();
            
            if (user != null) {
                List<Lists> userLists = listsService.getListsByUser(user);
                for (Lists lst : userLists) {
                    Map<String, Object> listInfo = new HashMap<>();
                    listInfo.put("id", lst.getId());
                    listInfo.put("name", lst.getName());
                    listInfo.put("restaurantId", restaurant.getId());
                    
                    boolean contains = false;
                    for (Restaurant r : lst.getRestaurants()) {
                        if (r.getId().equals(restaurant.getId())) {
                            contains = true;
                            break;
                        }
                    }
                    listInfo.put("contains", contains);
                    if (contains) isSavedInAnyList = true;
                    listsStatus.add(listInfo);
                }
            }
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("isSavedInAnyList", isSavedInAnyList);
            model.addAttribute("userListsWithStatus", listsStatus);

            // 2. MAGIA NUEVA: Lógica de Restaurantes Recomendados (misma especialidad)
            List<Map<String, Object>> allRestData = getRestaurantsWithListStatus(user, null);
            List<Map<String, Object>> recommended = new ArrayList<>();
            for(Map<String, Object> rMap : allRestData) {
                // Si es de la misma especialidad y NO es el mismo restaurante que estamos viendo...
                if(rMap.get("specialty") != null && 
                   rMap.get("specialty").equals(restaurant.getSpecialty()) && 
                   !rMap.get("id").equals(restaurant.getId())) {
                    recommended.add(rMap);
                }
            }
            model.addAttribute("recommendedRestaurants", recommended);
        }
        return "details"; 
    }

    private List<Map<String, Object>> getRestaurantsWithListStatus(User user, String query) {
        List<Map<String, Object>> restaurantsData = new ArrayList<>();
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        
        if (query != null && !query.trim().isEmpty()) {
            String q = query.toLowerCase();
            allRestaurants = allRestaurants.stream()
                .filter(r -> (r.getMunicipality() != null && r.getMunicipality().toLowerCase().contains(q)) || 
                             (r.getName() != null && r.getName().toLowerCase().contains(q)) ||
                             (r.getSpecialty() != null && r.getSpecialty().toLowerCase().contains(q)))
                .toList();
        }

        List<Lists> userLists = (user != null) ? listsService.getListsByUser(user) : new ArrayList<>();

        for (Restaurant r : allRestaurants) {
            Map<String, Object> rData = new HashMap<>();
            rData.put("id", r.getId());
            rData.put("name", r.getName());
            rData.put("specialty", r.getSpecialty());
            rData.put("municipality", r.getMunicipality());
            rData.put("averagePrice", r.getAveragePrice());
            rData.put("description", r.getDescription());
            
            boolean isSavedInAnyList = false;
            List<Map<String, Object>> listsStatus = new ArrayList<>();
            
            if (user != null) {
                for (Lists lst : userLists) {
                    Map<String, Object> listInfo = new HashMap<>();
                    listInfo.put("id", lst.getId());
                    listInfo.put("name", lst.getName());
                    listInfo.put("restaurantId", r.getId()); 
                    
                    boolean contains = false;
                    for (Restaurant listRest : lst.getRestaurants()) {
                        if (listRest.getId().equals(r.getId())) {
                            contains = true;
                            break;
                        }
                    }
                    listInfo.put("contains", contains); 
                    if (contains) {
                        isSavedInAnyList = true; 
                    }
                    listsStatus.add(listInfo);
                }
            }
            rData.put("isSavedInAnyList", isSavedInAnyList);
            rData.put("userListsWithStatus", listsStatus);
            restaurantsData.add(rData);
        }
        return restaurantsData;
    }
}