package es.urjc.repositories;

import es.urjc.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByMunicipalityIgnoreCase(String municipality);

    List<Restaurant> findByNameContainingIgnoreCaseOrSpecialtyContainingIgnoreCase(String name, String specialty);

    List<Restaurant> findByMunicipalityIgnoreCaseAndSpecialtyIgnoreCase(String municipality, String specialty);
}