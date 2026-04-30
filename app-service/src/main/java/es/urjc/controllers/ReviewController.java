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
            User currentUser = userService.findByUsername(principal.getName()).orElseThrow();
            Restaurant currentRestaurant = restaurantRepository.findById(restaurantId).orElseThrow();
            Review newReview = new Review(comment, rating, currentUser, currentRestaurant);
            reviewService.saveReview(newReview);
        } catch (Exception e) {
            return "redirect:/restaurant/" + restaurantId + "?error=true";
        }
        return "redirect:/restaurant/" + restaurantId;
    }

    @PostMapping("/review/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId, Principal principal) {
        if (principal == null) return "redirect:/login";

        try {
            User currentUser = userService.findByUsername(principal.getName()).orElseThrow();
            reviewService.deleteReview(reviewId, currentUser);
        } catch (Exception e) {
            return "redirect:/profile?error=true";
        }

        return "redirect:/profile";
    }

    @PostMapping("/admin/reviews/{reviewId}/delete")
    public String adminDeleteReview(@PathVariable Long reviewId, Principal principal) {
        // Vamos al login si no hay sesion
        if (principal == null) return "redirect:/loginadmin";

        try {
            // RECORDATORIO: Spring security deberia estar protegiendo las URL que empiezan por /admin/**
            reviewService.deleteReviewAdmin(reviewId);
        } catch (Exception e) {
            return "redirect:/admin/users?error=true";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/review/{reviewId}/edit")
    public String editReview(
            @PathVariable Long reviewId,
            @RequestParam int rating,
            @RequestParam String comment,
            Principal principal) {

        if (principal == null) return "redirect:/login";

        User currentUser = userService.findByUsername(principal.getName()).orElseThrow();
        reviewService.editReview(reviewId, rating, comment, currentUser);

        return "redirect:/profile";
    }
}