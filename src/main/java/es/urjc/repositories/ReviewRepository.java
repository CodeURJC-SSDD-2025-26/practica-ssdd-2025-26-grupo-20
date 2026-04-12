package es.urjc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import es.urjc.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{

}
