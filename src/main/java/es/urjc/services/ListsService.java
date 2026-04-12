package es.urjc.services;

import org.springframework.stereotype.Service;
import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.model.User;
import es.urjc.repositories.ListsRepository;

import java.util.*;

@Service
public class ListsService {

    private final ListsRepository listsRepository;

    public ListsService(ListsRepository listsRepository) {
        this.listsRepository = listsRepository;
    }

    // 1. Guardar una nueva lista
    public void saveList(Lists list) {
        listsRepository.save(list);
    }

    // 2. Buscar las listas de un usuario específico
    public List<Lists> getListsByUser(User user) {
        return listsRepository.findByOwner(user);
    }

    // 3. Buscar una lista por su ID
    public Optional<Lists> getListById(Long id) {
        return listsRepository.findById(id);
    }

    // 4. Borrar una lista
    public void deleteList(Long id) {
        listsRepository.deleteById(id);
    }

    // 5. Lógica N:M - Añadir un restaurante a la lista
    public void addRestaurantToList(Lists list, Restaurant restaurant) {
        // Comprobamos que el restaurante no esté ya en la lista para no duplicarlo
        if (!list.getRestaurants().contains(restaurant)) {
            list.getRestaurants().add(restaurant);
            listsRepository.save(list); // Actualizamos la base de datos
        }
    }

    // 6. Lógica N:M - Quitar un restaurante de la lista
    public void removeRestaurantFromList(Lists list, Restaurant restaurant) {
        list.getRestaurants().remove(restaurant);
        listsRepository.save(list); // Actualizamos la base de datos
    }
}