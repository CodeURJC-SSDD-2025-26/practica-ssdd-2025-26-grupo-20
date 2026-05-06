package es.urjc.controllers.rest;

import es.urjc.dto.RestaurantDTO;
import es.urjc.model.Restaurant;
import es.urjc.services.RestaurantService;
import es.urjc.services.ReviewService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.sql.Blob;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantRestController {

    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    public RestaurantRestController(RestaurantService restaurantService, ReviewService reviewService) {
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
    }

    // GET /api/v1/restaurants?page=0&size=10&query=...&municipality=...&specialty=...
    @GetMapping
    public ResponseEntity<Page<RestaurantDTO>> getRestaurants(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) String specialty,
            Pageable pageable) {

        Page<RestaurantDTO> page = restaurantService
                .searchPaged(query, municipality, specialty, pageable)
                .map(this::toDTO);

        return ResponseEntity.ok(page);
    }

    // GET /api/v1/restaurants/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable Long id) {

        return restaurantService.findById(id)
                .map(r -> ResponseEntity.ok(toDTO(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/v1/restaurants
    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(
            @RequestBody RestaurantDTO dto,
            Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();
        if (!isAdmin())        return ResponseEntity.status(403).build();

        try {
            Restaurant restaurant = fromDTO(dto);
            Restaurant saved = restaurantService.save(restaurant);
            URI location = URI.create("/api/v1/restaurants/" + saved.getId());
            return ResponseEntity.created(location).body(toDTO(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/v1/restaurants/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantDTO dto,
            Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();
        if (!isAdmin())        return ResponseEntity.status(403).build();

        if (restaurantService.findById(id).isEmpty())
            return ResponseEntity.notFound().build();

        try {
            Restaurant updated = restaurantService.update(id, fromDTO(dto));
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/v1/restaurants/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();
        if (!isAdmin())        return ResponseEntity.status(403).build();

        if (restaurantService.findById(id).isEmpty())
            return ResponseEntity.notFound().build();

        restaurantService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/restaurants/{id}/image
    @GetMapping("/{id}/image")
    public void getImage(@PathVariable Long id, HttpServletResponse response) {
        try {
            Restaurant restaurant = restaurantService.findById(id).orElse(null);
            if (restaurant == null || restaurant.getImageFile() == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Blob blob = restaurant.getImageFile();
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            response.setContentType("image/jpeg");
            response.getOutputStream().write(bytes);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // PUT /api/v1/restaurants/{id}/image
    @PutMapping("/{id}/image")
    public ResponseEntity<RestaurantDTO> updateImage(
            @PathVariable Long id,
            @RequestParam("imageFile") MultipartFile imageFile,
            Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();
        if (!isAdmin())        return ResponseEntity.status(403).build();

        Restaurant restaurant = restaurantService.findById(id).orElse(null);
        if (restaurant == null) return ResponseEntity.notFound().build();

        try {
            Restaurant updated = restaurantService.saveWithImage(restaurant, imageFile);
            return ResponseEntity.ok(toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private RestaurantDTO toDTO(Restaurant r) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setMunicipality(r.getMunicipality());
        dto.setSpecialty(r.getSpecialty());
        dto.setAveragePrice(r.getAveragePrice());
        dto.setAddress(r.getAddress());
        dto.setPhone(r.getPhone());
        dto.setDescription(r.getDescription());
        dto.setHasImage(r.isHasImage());
        return dto;
    }

    private Restaurant fromDTO(RestaurantDTO dto) {
        Restaurant r = new Restaurant();
        r.setName(dto.getName());
        r.setMunicipality(dto.getMunicipality());
        r.setSpecialty(dto.getSpecialty());
        r.setAveragePrice(dto.getAveragePrice());
        r.setAddress(dto.getAddress());
        r.setPhone(dto.getPhone());
        r.setDescription(dto.getDescription());
        return r;
    }
}