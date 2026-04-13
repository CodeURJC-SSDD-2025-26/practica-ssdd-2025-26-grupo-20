package es.urjc.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
public class AdminControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testShowAdminRestaurantsPage() throws Exception {
        mockMvc.perform(get("/admin/restaurants"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-restaurants"))
            .andExpect(model().attributeExists("restaurants"))
            .andExpect(model().attributeExists("restaurantForm"));
    }

    @Test
    public void testCreateRestaurant() throws Exception {
        mockMvc.perform(post("/admin/restaurants/new")
                .param("name", "Restaurante Test")
                .param("municipality", "Madrid")
                .param("specialty", "Italiana")
                .param("averagePrice", "20")
                .param("address", "Gran Via 10")
                .param("phone", "912345678")
                .param("description", "Restaurante de prueba"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/restaurants"));
    }

    @Test
    public void testDeleteRestaurant() throws Exception {
        mockMvc.perform(post("/admin/restaurants/1/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/restaurants"));
    }

    // meter este test cuando sepa in id real de la base de datos testUpdateRestaurant()
}