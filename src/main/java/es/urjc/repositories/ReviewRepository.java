package es.urjc.repositories;

import es.urjc.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import es.urjc.model.Restaurant;

public interface ReviewRepository extends JpaRepository<Review, Long>{

    // Guardamos el nombre del restaurante y su nota media
    interface RestaurantStats {
        String getName();
        Double getAverageRating();
    }
    // Le pedimos a la bade de datos que junte las reseñas con los restaurantes, y calcule la media
    // y que los ordene de mayor a menor y que nos devuelva los 5 primeros
    @Query(value = "SELECT rest.name AS name, AVG(rev.rating) AS averageRating " +
                    "FROM review rev JOIN restaurant rest ON rev.restaurant_id = rest.id " + 
                    "GROUP BY rest.id, rest.name " + 
                    "ORDER BY averageRating DESC LIMIT 5", 
            nativeQuery = true)
    List<RestaurantStats> findTop5Restaurants();
    List<Review> findByRestaurant(Restaurant restaurant);
}
