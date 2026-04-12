package es.urjc.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.model.User;
import es.urjc.repositories.ListsRepository;

public class ListsServiceTest {

    @Mock
    private ListsRepository listsRepository;

    @InjectMocks
    private ListsService listsService;

    // Creamos unos objetos de prueba para no repetir código
    private User testUser;
    private Lists testList;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(); // Simulamos un usuario
        testList = new Lists("Mis Favoritos", "Los mejores sitios", testUser);
        testRestaurant = new Restaurant();
    }

    @Test
    void testSaveList() {
        // Probamos que al guardar, se llame al repositorio correctamente
        listsService.saveList(testList);
        verify(listsRepository, times(1)).save(testList);
    }

    @Test
    void testGetListsByUser() {
        // Simulamos que la base de datos devuelve una lista con nuestro elemento
        List<Lists> mockDatabase = new ArrayList<>();
        mockDatabase.add(testList);
        when(listsRepository.findByOwner(testUser)).thenReturn(mockDatabase);

        // Ejecutamos tu función
        List<Lists> result = listsService.getListsByUser(testUser);

        // Comprobamos que funciona
        assertEquals(1, result.size(), "Debería devolver 1 lista");
        assertEquals("Mis Favoritos", result.get(0).getName(), "El nombre de la lista debe coincidir");
    }

    @Test
    void testDeleteList() {
        // Probamos la orden de destrucción
        Long idToDelete = 1L;
        listsService.deleteList(idToDelete);
        verify(listsRepository, times(1)).deleteById(idToDelete);
    }

    @Test
    void testAddRestaurantToList() {
        listsService.addRestaurantToList(testList, testRestaurant);
        assertTrue(testList.getRestaurants().contains(testRestaurant), "El restaurante debería añadirse");
        verify(listsRepository, times(1)).save(testList);
    }

    @Test
    void testRemoveRestaurantFromList() {
        testList.getRestaurants().add(testRestaurant); // Lo metemos primero a la fuerza
        listsService.removeRestaurantFromList(testList, testRestaurant); // Usamos tu función para sacarlo
        assertFalse(testList.getRestaurants().contains(testRestaurant), "El restaurante debería desaparecer");
        verify(listsRepository, times(1)).save(testList);
    }
}