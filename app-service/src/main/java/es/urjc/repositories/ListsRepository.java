package es.urjc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.urjc.model.Lists;
import es.urjc.model.User;

import java.util.List;

public interface ListsRepository extends JpaRepository<Lists, Long> {

    List<Lists> findByOwner(User owner);

    // Borra las filas de la tabla intermedia lists_restaurants
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lists_restaurants WHERE included_in_lists_id = :listId", nativeQuery = true)
    void deleteRestaurantsFromList(@Param("listId") Long listId);

    // Borra la lista en si
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lists WHERE id = :listId", nativeQuery = true)
    void deleteListById(@Param("listId") Long listId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lists_restaurants WHERE restaurants_id = :restaurantId", nativeQuery = true)
    void deleteRestaurantFromAllLists(@Param("restaurantId") Long restaurantId);
}