package es.urjc.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.urjc.model.Lists;
import es.urjc.model.Restaurant;
import es.urjc.model.Review;
import es.urjc.model.User;
import es.urjc.repositories.ListsRepository;
import es.urjc.repositories.RestaurantRepository;
import es.urjc.repositories.UserRepository;
import es.urjc.repositories.ReviewRepository;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ListsRepository listsRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(RestaurantRepository restaurantRepository, UserRepository userRepository,
                                ListsRepository listsRepository, ReviewRepository reviewRepository,
                                PasswordEncoder passwordEncoder) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.listsRepository = listsRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (restaurantRepository.count() == 0) {

            // 1. Restaurantes
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

            // 2. Usuarios (contraseñas hasheadas con BCrypt)
            User admin = new User(
                "Cristian", "Admin", "admin@celisud.com", "admin",
                passwordEncoder.encode("admin123"),
                "Administrador del sistema", "ADMIN", "USER"
            );

            User chicote = new User(
                "Alberto", "Chicote", "alberto@gmail.com", "chicote_gluten",
                passwordEncoder.encode("1234"),
                "Celíaco desde 2018. Buscando los mejores rincones sin gluten.", "USER"
            );

            userRepository.save(admin);
            userRepository.save(chicote);

            // 3. Listas de prueba
            Lists listaFavoritos = new Lists("Favoritos", "Mis restaurantes top de Madrid", chicote);
            listaFavoritos.getRestaurants().add(pizzaNatura);

            Lists listaPorVisitar = new Lists("Por visitar", "Sitios a los que quiero ir este finde", chicote);
            listaPorVisitar.getRestaurants().add(okashiSanda);

            listsRepository.save(listaFavoritos);
            listsRepository.save(listaPorVisitar);

            // 4. Reseñas de prueba
            Review review1 = new Review("Pizza de puta mierda la odio joder me cago en todo y en la puta de tu madre. Repetiria", 5, chicote, pizzaNatura);
            Review review2 = new Review("Sushi caro y encima contaminao", 2, chicote, okashiSanda);
            Review review3 = new Review("Local muy bonito y seguro para celíacos.", 4, chicote, okashiSanda);

            reviewRepository.save(review1);
            reviewRepository.save(review2);
            reviewRepository.save(review3);

            System.out.println("Base de datos inicializada con restaurantes, usuarios y listas");
        }
    }
}