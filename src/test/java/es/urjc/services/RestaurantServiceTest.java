package es.urjc.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.urjc.model.Restaurant;

@SpringBootTest
@Transactional
public class RestaurantServiceTest {

    @Autowired
    private RestaurantService restaurantService;

    @Test
    public void testSaveRestaurant() {
        Restaurant restaurant = new Restaurant(
            "La Tagliatella",
            "Madrid",
            "Italiana",
            20.0,
            "Gran Via 10",
            "912345678",
            "Restaurante italiano sin gluten"
        );

        Restaurant savedRestaurant = restaurantService.save(restaurant);

        assertNotNull(savedRestaurant);
        assertNotNull(savedRestaurant.getId());
        assertEquals("La Tagliatella", savedRestaurant.getName());
    }

    @Test
    public void testSaveRestaurantWithoutName() {
        Restaurant restaurant = new Restaurant(
            "",
            "Madrid",
            "Italiana",
            20.0,
            "Gran Via 10",
            "912345678",
            "Restaurante italiano sin gluten"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            restaurantService.save(restaurant);
        });
    }

    @Test
    public void testUpdateRestaurant() {
        Restaurant restaurant = new Restaurant(
            "Burguer Test",
            "Madrid",
            "Americana",
            14.0,
            "Calle Uno 1",
            "933333333",
            "Hamburguesas"
        );

        Restaurant savedRestaurant = restaurantService.save(restaurant);

        Restaurant updatedRestaurant = new Restaurant(
            "Burguer Test Updated",
            "Madrid",
            "Americana",
            16.0,
            "Calle Uno 2",
            "944444444",
            "Hamburguesas actualizadas"
        );

        Restaurant result = restaurantService.update(savedRestaurant.getId(), updatedRestaurant);

        assertNotNull(result);
        assertEquals("Burguer Test Updated", result.getName());
        assertEquals(16.0, result.getAveragePrice());
        assertEquals("Calle Uno 2", result.getAddress());
    }
}