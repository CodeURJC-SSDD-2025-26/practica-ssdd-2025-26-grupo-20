package es.urjc.controllers.rest;

import es.urjc.repositories.ReviewRepository;
import es.urjc.services.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/charts")
public class ChartsRestController {

    private final ReviewService reviewService;

    public ChartsRestController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // GET /api/v1/charts/top-restaurants
    // Returns top 5 restaurants by average rating (data for bar/line chart)
    @GetMapping("/top-restaurants")
    public ResponseEntity<List<Map<String, Object>>> getTopRestaurants() {
        List<ReviewRepository.RestaurantStats> stats = reviewService.getTop5Restaurants();

        List<Map<String, Object>> result = stats.stream()
                .map(s -> Map.<String, Object>of(
                        "name", s.getName(),
                        "averageRating", s.getAverageRating()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}