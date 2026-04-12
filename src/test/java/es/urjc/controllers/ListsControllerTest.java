package es.urjc.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.model.User;
import es.urjc.repositories.RestaurantRepository;
import es.urjc.services.ListsService;
import es.urjc.services.UserService;

@SpringBootTest
@Transactional // Borra cualquier cambio que haga el test al terminar
public class ListsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private ListsService listsService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testShowProfilePage() throws Exception {
        mockMvc.perform(get("/profile"))
               .andExpect(status().isOk())
               .andExpect(view().name("profile"));
    }

    @Test
    public void testCreateNewListEmptyName() throws Exception {
        mockMvc.perform(post("/lists/create")
                .param("name", "   ") 
                .param("description", "Mala lista"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/profile?error=NombreVacio")); 
    }
    
    @Test
    public void testCreateNewListSuccess() throws Exception {
        mockMvc.perform(post("/lists/create")
                .param("name", "Test List") 
                .param("description", "Para probar"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/profile")); 
    }

    // --- LOS DOS TESTS QUE FALTABAN, AHORA CON LÓGICA REAL ---

    @Test
    public void testShowListDetailsPage() throws Exception {
        // 1. Buscamos al usuario real (que lo mete tu DatabaseInitializer)
        User user = userService.findByUsername("chicote_gluten").orElseThrow();
        
        // 2. Creamos una lista temporal en la BD real
        Lists testList = new Lists("Lista de Detalles", "Prueba", user);
        listsService.saveList(testList);
        
        // 3. Obtenemos el ID real que le haya puesto MySQL
        Long realListId = testList.getId();

        // 4. Comprobamos la página del OJO con ese ID
        mockMvc.perform(get("/lista/" + realListId))
               .andExpect(status().isOk())
               .andExpect(view().name("listdetails")); 
    }

    @Test
    public void testToggleRestaurantAJAX() throws Exception {
        // 1. Preparamos el terreno con datos reales
        User user = userService.findByUsername("chicote_gluten").orElseThrow();
        
        Lists testList = new Lists("Lista AJAX", "Prueba", user);
        listsService.saveList(testList);
        
        Restaurant testRestaurant = new Restaurant();
        testRestaurant.setName("Restaurante de Prueba");
        testRestaurant.setMunicipality("Madrid");
        restaurantRepository.save(testRestaurant);

        // 2. Obtenemos los IDs reales
        Long realListId = testList.getId();
        Long realRestId = testRestaurant.getId();

        // 3. Simulamos pulsar el botón de AJAX (+)
        mockMvc.perform(post("/lists/" + realListId + "/toggleRestaurant/" + realRestId))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/restaurants"));
    }
}