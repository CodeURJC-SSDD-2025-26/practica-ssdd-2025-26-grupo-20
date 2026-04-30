package es.urjc.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.model.Review;
import es.urjc.model.User;
import es.urjc.services.ListsService;
import es.urjc.services.RestaurantService;
import es.urjc.services.UserService;
import es.urjc.services.ReviewService;

@Controller
public class WebController {

    
    private final UserService userService;
    private final ListsService listsService;
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;
    

    public WebController(UserService userService,ListsService listsService, RestaurantService restaurantService, ReviewService reviewService) {
        this.userService = userService;
        this.listsService = listsService;
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
    }

    @GetMapping("/restaurants")
    public String showRestaurants(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) String specialty,
            Model model,
            Principal principal) {

        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("isAuthenticated", true);
                model.addAttribute("hasAvatar", user.getAvatarImage() != null);
            }
        }

        List<Restaurant> filteredRestaurants = restaurantService.search(query, municipality, specialty);
        model.addAttribute("restaurants", getRestaurantsWithListStatus(user, filteredRestaurants));

        model.addAttribute("query", query);
        model.addAttribute("municipality", municipality);
        model.addAttribute("specialty", specialty);

        return "restaurants";
    }
    
    @GetMapping("/")
public String showIndex(Model model, Principal principal) {
    User user = null;

    if (principal != null) {
        user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("hasAvatar", user.getAvatarImage() != null);
            model.addAttribute("isAuthenticated", true);
            
            boolean isAdmin = false;
            if (principal instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken) {
                isAdmin = ((org.springframework.security.authentication.UsernamePasswordAuthenticationToken) principal)
                    .getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            }
            model.addAttribute("isAdmin", isAdmin);
        }
    }

    List<Restaurant> allRestaurants = restaurantService.findAll();
    List<Map<String, Object>> restaurantsData = getRestaurantsWithListStatus(user, allRestaurants);
    model.addAttribute("restaurants", restaurantsData);

    List<Map<String, Object>> recommendedRestaurants = new ArrayList<>();
    for (Map<String, Object> restaurantData : restaurantsData) {
        recommendedRestaurants.add(restaurantData);
        if (recommendedRestaurants.size() == 6) break;
    }
    model.addAttribute("recommendedRestaurants", recommendedRestaurants);

    return "index";
}

    @GetMapping("/restaurant/{id}")
    public String showRestaurantDetails(@PathVariable Long id, Model model, Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("isAuthenticated", true);
                model.addAttribute("hasAvatar", user.getAvatarImage() != null);
            }
        }
        Restaurant restaurant = restaurantService.findById(id).orElse(null);
        if (restaurant != null) {
            List<Review> reviews = reviewService.findByRestaurant(restaurant);
            model.addAttribute("reviews", reviews);
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

            List<Map<String, Object>> allRestData = getRestaurantsWithListStatus(user, restaurantService.findAll());
            List<Map<String, Object>> recommended = new ArrayList<>();
            for (Map<String, Object> rMap : allRestData) {
                if (rMap.get("specialty") != null &&
                    rMap.get("specialty").equals(restaurant.getSpecialty()) &&
                    !rMap.get("id").equals(restaurant.getId())) {
                    recommended.add(rMap);
                }
            }
            model.addAttribute("recommendedRestaurants", recommended);
        }
        return "details";
    }

    private List<Map<String, Object>> getRestaurantsWithListStatus(User user, List<Restaurant> restaurants) {
        List<Map<String, Object>> restaurantsData = new ArrayList<>();

        List<Lists> userLists = (user != null) ? listsService.getListsByUser(user) : new ArrayList<>();

        for (Restaurant r : restaurants) {
            Map<String, Object> rData = new HashMap<>();
            rData.put("id", r.getId());
            rData.put("name", r.getName());
            rData.put("specialty", r.getSpecialty());
            rData.put("municipality", r.getMunicipality());
            rData.put("averagePrice", r.getAveragePrice());
            rData.put("description", r.getDescription());
            rData.put("hasImage", r.isHasImage());

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

            rData.put("isAuthenticated", user != null);
            rData.put("isSavedInAnyList", isSavedInAnyList);
            rData.put("userListsWithStatus", listsStatus);
            restaurantsData.add(rData);
        }

        return restaurantsData;
    }

}