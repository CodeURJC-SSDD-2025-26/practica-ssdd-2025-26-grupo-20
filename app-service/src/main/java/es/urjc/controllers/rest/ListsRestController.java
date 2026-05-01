package es.urjc.controllers.rest;

import es.urjc.dto.ListsDTO;
import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.model.User;
import es.urjc.services.ListsService;
import es.urjc.services.RestaurantService;
import es.urjc.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/lists")
public class ListsRestController {

    private static final Logger log = LoggerFactory.getLogger(ListsRestController.class);

    private final ListsService listsService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    public ListsRestController(ListsService listsService, UserService userService, RestaurantService restaurantService) {
        this.listsService = listsService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    // GET /api/v1/lists?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<ListsDTO>> getMyLists(Pageable pageable, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        Page<ListsDTO> page = listsService.getListsByUserPaged(user, pageable).map(this::toDTO);
        return ResponseEntity.ok(page);
    }

    // GET /api/v1/lists/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ListsDTO> getList(@PathVariable Long id, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        Optional<Lists> opt = listsService.getListById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Lists list = opt.get();
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null || !list.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(toDTO(list));
    }

    // POST /api/v1/lists
    @PostMapping
    public ResponseEntity<ListsDTO> createList(@RequestBody ListsDTO dto, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        if (dto.getName() == null || dto.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        Lists newList = new Lists(dto.getName(), dto.getDescription(), user);
        listsService.saveList(newList);

        log.info("User {} created list '{}'", user.getUsername(), newList.getName());
        URI location = URI.create("/api/v1/lists/" + newList.getId());
        return ResponseEntity.created(location).body(toDTO(newList));
    }

    // PUT /api/v1/lists/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ListsDTO> updateList(@PathVariable Long id,
                                                @RequestBody ListsDTO dto,
                                                Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        Optional<Lists> opt = listsService.getListById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Lists list = opt.get();
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null || !list.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            list.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            list.setDescription(dto.getDescription());
        }

        listsService.saveList(list);
        return ResponseEntity.ok(toDTO(list));
    }

    // DELETE /api/v1/lists/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            listsService.deleteList(id, user);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Delete list failed: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    // POST /api/v1/lists/{listId}/restaurants/{restaurantId}
    @PostMapping("/{listId}/restaurants/{restaurantId}")
    public ResponseEntity<ListsDTO> addRestaurant(@PathVariable Long listId,
                                                   @PathVariable Long restaurantId,
                                                   Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        Optional<Lists> optList = listsService.getListById(listId);
        Optional<Restaurant> optRestaurant = restaurantService.findById(restaurantId);

        if (optList.isEmpty() || optRestaurant.isEmpty()) return ResponseEntity.notFound().build();

        Lists list = optList.get();
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null || !list.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        listsService.addRestaurantToList(list, optRestaurant.get());
        return ResponseEntity.ok(toDTO(list));
    }

    // DELETE /api/v1/lists/{listId}/restaurants/{restaurantId}
    @DeleteMapping("/{listId}/restaurants/{restaurantId}")
    public ResponseEntity<ListsDTO> removeRestaurant(@PathVariable Long listId,
                                                      @PathVariable Long restaurantId,
                                                      Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        Optional<Lists> optList = listsService.getListById(listId);
        Optional<Restaurant> optRestaurant = restaurantService.findById(restaurantId);

        if (optList.isEmpty() || optRestaurant.isEmpty()) return ResponseEntity.notFound().build();

        Lists list = optList.get();
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null || !list.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        listsService.removeRestaurantFromList(list, optRestaurant.get());
        return ResponseEntity.ok(toDTO(list));
    }

    private ListsDTO toDTO(Lists list) {
        ListsDTO dto = new ListsDTO();
        dto.setId(list.getId());
        dto.setName(list.getName());
        dto.setDescription(list.getDescription());
        dto.setOwnerId(list.getOwner().getId());
        dto.setOwnerUsername(list.getOwner().getUsername());
        dto.setRestaurantIds(
            list.getRestaurants().stream().map(Restaurant::getId).toList()
        );
        return dto;
    }
}