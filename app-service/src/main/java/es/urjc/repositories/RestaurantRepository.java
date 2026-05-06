package es.urjc.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.urjc.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByMunicipalityContainingIgnoreCase(String municipality);

    List<Restaurant> findBySpecialtyIgnoreCase(String specialty);

    List<Restaurant> findByNameContainingIgnoreCaseOrSpecialtyContainingIgnoreCase(String name, String specialty);

    List<Restaurant> findByMunicipalityContainingIgnoreCaseAndSpecialtyIgnoreCase(String municipality, String specialty);
    
    Page<Restaurant> findByMunicipalityContainingIgnoreCaseAndSpecialtyIgnoreCase(String municipality, String specialty, Pageable pageable);

    Page<Restaurant> findByMunicipalityContainingIgnoreCase(String municipality, Pageable pageable);

    Page<Restaurant> findBySpecialtyIgnoreCase(String specialty, Pageable pageable);

    Page<Restaurant> findByNameContainingIgnoreCaseOrSpecialtyContainingIgnoreCase(String name, String specialty, Pageable pageable);
}