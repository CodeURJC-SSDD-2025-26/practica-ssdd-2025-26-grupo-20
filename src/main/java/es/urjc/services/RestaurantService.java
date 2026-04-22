package es.urjc.services;

import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.repositories.ListsRepository;
import es.urjc.repositories.RestaurantRepository;
import es.urjc.repositories.ReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
public class RestaurantService {

    @Autowired
    private ListsRepository listsRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant save(Restaurant restaurant) {
        validateRestaurant(restaurant);
        return restaurantRepository.save(restaurant);
    }

    public Restaurant update(Long id, Restaurant updatedRestaurant) {
        validateRestaurant(updatedRestaurant);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        restaurant.setName(updatedRestaurant.getName());
        restaurant.setMunicipality(updatedRestaurant.getMunicipality());
        restaurant.setSpecialty(updatedRestaurant.getSpecialty());
        restaurant.setAveragePrice(updatedRestaurant.getAveragePrice());
        restaurant.setAddress(updatedRestaurant.getAddress());
        restaurant.setPhone(updatedRestaurant.getPhone());
        restaurant.setDescription(updatedRestaurant.getDescription());

        if (updatedRestaurant.getImageFile() != null) {
            restaurant.setImageFile(updatedRestaurant.getImageFile());
        }

        return restaurantRepository.save(restaurant);
    }

    @Autowired
    private ListsRepository ListsRepository; // o como se llame tu repo de Lists

    @Autowired  
    private ReviewRepository ReviewRepository; // o como se llame tu repo de Reviews

    @Transactional
    public void deleteById(Long id) {
        // 1. Borrar de la tabla join lists_restaurants
        listsRepository.deleteRestaurantFromAllLists(id);

        // 2. Borrar las reseñas con SQL nativo directo
        reviewRepository.deleteByRestaurantId(id);

        // 3. Borrar el restaurante
        restaurantRepository.deleteById(id);
    }

    public List<Restaurant> search(String query, String municipality, String specialty) {

        boolean hasQuery = query != null && !query.isBlank();
        boolean hasMunicipality = municipality != null && !municipality.isBlank();
        boolean hasSpecialty = specialty != null && !specialty.isBlank();

        if (hasMunicipality && hasSpecialty) {
            return restaurantRepository.findByMunicipalityContainingIgnoreCaseAndSpecialtyIgnoreCase(municipality, specialty);
        }

        if (hasMunicipality) {
            return restaurantRepository.findByMunicipalityContainingIgnoreCase(municipality);
        }

        if (hasSpecialty) {
            return restaurantRepository.findBySpecialtyIgnoreCase(specialty);
        }

        if (hasQuery) {
            return restaurantRepository.findByNameContainingIgnoreCaseOrSpecialtyContainingIgnoreCase(query, query);
        }

        return restaurantRepository.findAll();
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (restaurant.getName() == null || restaurant.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (restaurant.getMunicipality() == null || restaurant.getMunicipality().isBlank()) {
            throw new IllegalArgumentException("El municipio es obligatorio");
        }

        if (restaurant.getSpecialty() == null || restaurant.getSpecialty().isBlank()) {
            throw new IllegalArgumentException("La especialidad es obligatoria");
        }

        if (restaurant.getAddress() == null || restaurant.getAddress().isBlank()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }

        if (restaurant.getAveragePrice() <= 0) {
            throw new IllegalArgumentException("El precio medio debe ser mayor que 0");
        }
    }

}