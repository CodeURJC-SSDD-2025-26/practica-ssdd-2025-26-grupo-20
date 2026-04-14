package es.urjc.controllers;

import es.urjc.model.Restaurant;
import es.urjc.model.Review;
import es.urjc.model.User;
import es.urjc.services.ReviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {

    private final DaoAuthenticationProvider authenticationProvider;
    @Autowired
    private ReviewService reviewService;
    //Luego tengo que añadir los demas servicios

    ReviewController(DaoAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    //Metodo para saber cuando alguien envia un formulario
    @PostMapping("/restaurant/{restaurantId}/addReview")
    public String addReview(
        @PathVariable Long restaurantId, // El ID del restaurante de la URL
        @RequestParam int rating,        // La nota que viene del formulario HTML
        @RequestParam String comment     // El texto que viene del formulario HTML
    ) {

        try {
            // 1. Aquí (cuando tu compañero 1 termine) cogeremos al usuario que ha iniciado sesión.
            // De momento, nos inventamos uno para que no dé error.
            User usuarioActual = new User();

            // 2. Aquí lo mismo pero con restaurante. Y lo buscaremos por su ID
            Restaurant restauranteActual = new Restaurant();

            // 3. Creamos la reseña 
            Review nuevaResena = new Review(comment, rating, usuarioActual, restauranteActual);

            // 4. La guardamos en el service
            reviewService.saveReview(nuevaResena);
        } catch (IllegalArgumentException e) {
            // Si el servicio se queja (ej. nota mayor a 5), podríamos redirigir a una página de error
            // De momento, si falla, simplemente volvemos al restaurante
            return "redirect:/restaurant/" + restaurantId + "?error=true";
        }

        // 5. Volvemos a la pagina del restaurante
        return "redirect:/restaurant/" + restaurantId;
    }

    @GetMapping("/admin/restaurants")
    public String showStatistics(Model model) {

        var top5 = reviewService.getTop5Restaurants();

        model.addAttribute("topRestaurants", top5);

        return "admin-restaurants";
    }

}
