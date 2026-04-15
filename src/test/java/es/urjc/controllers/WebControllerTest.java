package es.urjc.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional // <- Esto borra cualquier cambio que haga el test al terminar
public class WebControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // Constructor limpio y sin dependencias raras que den error
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testShowIndexPage() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attributeExists("restaurants"));
    }
    
    @Test
    public void testShowRestaurantsPage() throws Exception {
        mockMvc.perform(get("/restaurants"))
            .andExpect(status().isOk())
            .andExpect(view().name("restaurants"))
            .andExpect(model().attributeExists("restaurants"));
    }
    @Test
    public void testShowRestaurantsPageWithFilters() throws Exception {
        mockMvc.perform(get("/restaurants")
                .param("query", "pizza")
                .param("municipality", "Madrid")
                .param("specialty", "Italiana"))
            .andExpect(status().isOk())
            .andExpect(view().name("restaurants"))
            .andExpect(model().attributeExists("restaurants"))
            .andExpect(model().attribute("query", "pizza"))
            .andExpect(model().attribute("municipality", "Madrid"))
            .andExpect(model().attribute("specialty", "Italiana"));
    }
    @Test
    public void testShowRestaurantDetailsPage() throws Exception {
        mockMvc.perform(get("/restaurant/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("details"));
    }
}