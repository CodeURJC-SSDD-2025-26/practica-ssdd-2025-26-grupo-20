package es.urjc.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void saveList(Lists list) {
        listsRepository.save(list);
    }

    public List<Lists> getListsByUser(User user) {
        return listsRepository.findByOwner(user);
    }

    public Optional<Lists> getListById(Long id) {
        return listsRepository.findById(id);
    }

    @Transactional
    public void deleteList(Long id) {
        // Borrado por SQL nativo para evitar problemas del estado JPA
        listsRepository.deleteRestaurantsFromList(id); // limpia tabla intermedia
        listsRepository.deleteListById(id);            // borra la lista
    }

    public void addRestaurantToList(Lists list, Restaurant restaurant) {
        if (!list.getRestaurants().contains(restaurant)) {
            list.getRestaurants().add(restaurant);
            listsRepository.save(list);
        }
    }

    public void removeRestaurantFromList(Lists list, Restaurant restaurant) {
        list.getRestaurants().remove(restaurant);
        listsRepository.save(list);
    }
}