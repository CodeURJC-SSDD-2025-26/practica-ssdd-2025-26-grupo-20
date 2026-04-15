package es.urjc.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.model.Review;
import es.urjc.model.User;
import es.urjc.repositories.ListsRepository;
import es.urjc.repositories.RestaurantRepository;
import es.urjc.repositories.ReviewRepository;
import es.urjc.repositories.UserRepository;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ListsRepository listsRepository; // ¡Añadimos tu repositorio!
    private final ReviewRepository reviewRepository;

    public DatabaseInitializer(RestaurantRepository restaurantRepository, UserRepository userRepository, ListsRepository listsRepository, ReviewRepository reviewRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.listsRepository = listsRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Solo ejecutamos esto si la base de datos está vacía para no duplicar datos
        if (restaurantRepository.count() == 0) {
            
            // 1. Creamos los Restaurantes
            Restaurant pizzaNatura = new Restaurant(
                "Pizza Natura", 
                "Madrid", 
                "Italiano", 
                20.0, 
                "Calle de Jovellanos, 6, Madrid", 
                "+34 910 55 55 55", 
                "El templo de la pizza saludable. Masas elaboradas artesanalmente con mijo y quinoa."
            );
            
            Restaurant okashiSanda = new Restaurant(
                "Okashi Sanda", 
                "Madrid", 
                "Japonés", 
                25.0, 
                "Calle de San Vicente Ferrer, 22, Madrid", 
                "+34 911 22 33 44", 
                "El primer japonés certificado sin gluten de Madrid."
            );
            
            restaurantRepository.save(pizzaNatura);
            restaurantRepository.save(okashiSanda);

            // 2. Creamos los Usuarios
            User admin = new User(
                "Cristian", "Admin", "admin@celisud.com", "admin2", "{noop}admin", 
                "Administrador del sistema", "ADMIN"
            );
            
            User chicote = new User(
                "Alberto", "Chicote", "alberto@gmail.com", "chicote_gluten", "{noop}1234", 
                "Celíaco desde 2018. Buscando los mejores rincones sin gluten.", "USER"
            );
            
            userRepository.save(admin);
            userRepository.save(chicote);

            // ==========================================
            // 3. ¡LO NUEVO! TUS LISTAS DE PRUEBA
            // ==========================================
            
            // Creamos una lista para Chicote
            Lists listaFavoritos = new Lists("Favoritos", "Mis restaurantes top de Madrid", chicote);
            // Le metemos un restaurante a la lista (Relación N:M en acción)
            listaFavoritos.getRestaurants().add(pizzaNatura); 
            
            // Creamos otra lista diferente
            Lists listaPorVisitar = new Lists("Por visitar", "Sitios a los que quiero ir este finde", chicote);
            // Le metemos otro restaurante
            listaPorVisitar.getRestaurants().add(okashiSanda);

            // Guardamos las listas en la base de datos
            listsRepository.save(listaFavoritos);
            listsRepository.save(listaPorVisitar);

            //Reseñas de prueba
            Review review1 = new Review("Pizza de puta mierda la odio joder me cago en todo y en la puta de tu madre. Repetiria", 5, chicote, pizzaNatura);
            Review review2 = new Review("Sushi caro y encima contaminao", 2, chicote, okashiSanda);
            Review review3 = new Review("Local muy bonito y seguro para celíacos.", 4, chicote, okashiSanda);

            reviewRepository.save(review1);
            reviewRepository.save(review2);
            reviewRepository.save(review3);

            System.out.println("¡Base de datos inicializada con restaurantes, usuarios y LISTAS!");
        }
    }
}