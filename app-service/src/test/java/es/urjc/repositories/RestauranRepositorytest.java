package es.urjc.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.urjc.model.Restaurant;

@SpringBootTest
@Transactional
public class RestauranRepositorytest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void testFindByMunicipalityIgnoreCase() {
        Restaurant restaurant = new Restaurant(
            "La Tagliatella",
            "Madrid",
            "Italiana",
            20.0,
            "Gran Via 10",
            "912345678",
            "Restaurante italiano"
        );

        restaurantRepository.save(restaurant);

        List<Restaurant> results = restaurantRepository.findByMunicipalityContainingIgnoreCase("madrid");

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    public void testFindByNameContainingIgnoreCaseOrSpecialtyContainingIgnoreCase() {
        Restaurant restaurant = new Restaurant(
            "Pizza Natura",
            "Madrid",
            "Italiana",
            15.0,
            "Calle Pizza 7",
            "922222222",
            "Pizza sin gluten"
        );

        restaurantRepository.save(restaurant);

        List<Restaurant> results = restaurantRepository
            .findByNameContainingIgnoreCaseOrSpecialtyContainingIgnoreCase("pizza", "pizza");

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    public void testFindByMunicipalityIgnoreCaseAndSpecialtyIgnoreCase() {
        Restaurant restaurant = new Restaurant(
            "Sushi Test",
            "Mostoles",
            "Japonesa",
            18.0,
            "Calle Falsa 123",
            "911111111",
            "Restaurante japonés"
        );

        restaurantRepository.save(restaurant);

        List<Restaurant> results = restaurantRepository
            .findByMunicipalityContainingIgnoreCaseAndSpecialtyIgnoreCase("mostoles", "japonesa");

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }
}