package es.urjc.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.model.Restaurant;
import es.urjc.model.Review;
import es.urjc.model.User;
import es.urjc.repositories.RestaurantRepository;
import es.urjc.services.ReviewService;
import es.urjc.services.UserService;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @PostMapping("/restaurant/{restaurantId}/review")
    public String addReview(
        @PathVariable Long restaurantId,
        @RequestParam int rating,
        @RequestParam String comment,
        Principal principal
    ) {
        if (principal == null) return "redirect:/login";

        try {
            User usuarioActual = userService.findByUsername(principal.getName()).orElseThrow();
            Restaurant restauranteActual = restaurantRepository.findById(restaurantId).orElseThrow();

            Review nuevaResena = new Review(comment, rating, usuarioActual, restauranteActual);
            reviewService.saveReview(nuevaResena);
        } catch (Exception e) {
            return "redirect:/restaurant/" + restaurantId + "?error=true";
        }
        return "redirect:/restaurant/" + restaurantId;
    }

    @PostMapping("/review/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId, Principal principal) {
        if (principal == null) return "redirect:/login";

        try {
            User usuarioActual = userService.findByUsername(principal.getName()).orElseThrow();
            reviewService.deleteReview(reviewId, usuarioActual);
        } catch (Exception e) {
            return "redirect:/profile?error=true";
        }

        return "redirect:/profile";
    }

    @PostMapping("/review/{reviewId}/edit")
    public String editReview(
            @PathVariable Long reviewId,
            @RequestParam int rating,
            @RequestParam String comment,
            Principal principal) {

        if (principal == null) return "redirect:/login";

        User usuarioActual = userService.findByUsername(principal.getName()).orElseThrow();
        reviewService.editReview(reviewId, rating, comment, usuarioActual);

        return "redirect:/profile";
    }
}