package es.urjc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.model.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    
} 