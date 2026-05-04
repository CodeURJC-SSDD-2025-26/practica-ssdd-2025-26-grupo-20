package es.urjc.controllers.rest;

import es.urjc.dto.ReviewDTO;
import es.urjc.model.Restaurant;
import es.urjc.model.Review;
import es.urjc.model.User;
import es.urjc.services.RestaurantService;
import es.urjc.services.ReviewService;
import es.urjc.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
public class ReviewRestController {

    private final ReviewService reviewService;
    private final RestaurantService restaurantService;
    private final UserService userService;

    public ReviewRestController(ReviewService reviewService,
                                RestaurantService restaurantService,
                                UserService userService) {
        this.reviewService = reviewService;
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    // GET /api/v1/restaurants/{id}/reviews?page=0&size=10
    @GetMapping("/restaurants/{id}/reviews")
    public ResponseEntity<Page<ReviewDTO>> getReviews(
            @PathVariable Long id,
            Pageable pageable) {

        Restaurant restaurant = restaurantService.findById(id).orElse(null);
        if (restaurant == null) return ResponseEntity.notFound().build();

        Page<ReviewDTO> page = reviewService
                .findByRestaurantPaged(restaurant, pageable)
                .map(this::toDTO);

        return ResponseEntity.ok(page);
    }

    // POST /api/v1/restaurants/{id}/reviews
    @PostMapping("/restaurants/{id}/reviews")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long id,
            @RequestBody ReviewDTO dto,
            Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();

        Restaurant restaurant = restaurantService.findById(id).orElse(null);
        if (restaurant == null) return ResponseEntity.notFound().build();

        User author = userService.findByUsername(principal.getName()).orElse(null);
        if (author == null) return ResponseEntity.status(401).build();

        try {
            Review created = reviewService.createReview(restaurant, dto.getRating(), dto.getComment(), author);
            URI location = URI.create("/api/v1/reviews/" + created.getId());
            return ResponseEntity.created(location).body(toDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/v1/reviews/{id}
    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewDTO dto,
            Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();

        Review review = reviewService.findById(id).orElse(null);
        if (review == null) return ResponseEntity.notFound().build();

        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null) return ResponseEntity.status(401).build();

        try {
            reviewService.editReview(id, dto.getRating(), dto.getComment(), currentUser);
            Review updated = reviewService.findById(id).orElseThrow();
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    // DELETE /api/v1/reviews/{id}
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();

        Review review = reviewService.findById(id).orElse(null);
        if (review == null) return ResponseEntity.notFound().build();

        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null) return ResponseEntity.status(401).build();

        try {
            if (isAdmin()) {
                reviewService.deleteReviewAdmin(id);
            } else {
                reviewService.deleteReview(id, currentUser);
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private ReviewDTO toDTO(Review r) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setComment(r.getComment());
        dto.setRating(r.getRating());
        dto.setAuthorId(r.getAuthor().getId());
        dto.setAuthorUsername(r.getAuthor().getUsername());
        dto.setRestaurantId(r.getRestaurant().getId());
        return dto;
    }
}