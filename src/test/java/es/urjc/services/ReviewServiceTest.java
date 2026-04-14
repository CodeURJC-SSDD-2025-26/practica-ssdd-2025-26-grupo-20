package es.urjc.services;

import es.urjc.model.Restaurant;
import es.urjc.model.Review;
import es.urjc.model.User;
import es.urjc.repositories.ReviewRepository;
import es.urjc.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    @Mock // Simulamos la base de datos (la despensa)
    private ReviewRepository reviewRepository;

    @InjectMocks // Este es nuestro Servicio real (el cocinero)
    private ReviewService reviewService;

    private User autorReal;
    private User usuarioFalso;
    private Review resena;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Preparamos los datos de prueba
        autorReal = new User("Alberto", "Chicote", "alberto@gmail.com", "achicote", "pass", "Chef");
        autorReal.setId(1L); // Le damos un ID ficticio

        usuarioFalso = new User("Paco", "Porras", "paco@gmail.com", "pporras", "pass", "Falso");
        usuarioFalso.setId(2L); // ID distinto

        Restaurant restaurante = new Restaurant("Pizza Natura", "Madrid", "Italiana", 20.0, "Calle 1", "123", "Buena pizza");
        
        resena = new Review("Muy buena", 5, autorReal, restaurante);
        resena.setId(100L); // ID ficticio de la reseña
    }

    @Test
    void borrarResena_SiendoElAutor_DeberiaFuncionar() {
        // Le decimos a nuestra base de datos simulada que cuando busque la reseña 100L, devuelva nuestra 'resena'
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(resena));

        // Intentamos borrar la reseña siendo el autor real (debería funcionar)
        assertDoesNotThrow(() -> reviewService.deleteReview(100L, autorReal));

        // Verificamos que se llamó al método delete del repositorio
        verify(reviewRepository, times(1)).delete(resena);
    }

    @Test
    void borrarResena_NoSiendoElAutor_DeberiaLanzarExcepcion() {
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(resena));

        // Intentamos borrar la reseña siendo el usuario falso. 
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(100L, usuarioFalso);
        });

        assertEquals("No tienes permiso para borrar esta reseña.", exception.getMessage());
        
        // Verificamos que NUNCA se llamó al repositorio para borrar
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void createReview_ValidData() {
        //Al guardar una reseña correcta no debería dar ningun error
        assertDoesNotThrow(() -> reviewService.saveReview(resena));

        //Comprobamos que guarda
        verify(reviewRepository, times(1)).save(resena);
    }

    @Test
    void createReview_InvalidRating() {
        //Ponemo una nota falsa
        resena.setRating(6);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.saveReview(resena);
        });

        assertEquals("La valoración debe estar entre 1 y 5.", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_NullComment() {
        //Quitamos el comentario
        resena.setComment(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.saveReview(resena);
        });

        assertEquals("El comentario no puede estar vacío.", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void getTop5Restaurants_ShouldCallRepository(){

        reviewService.getTop5Restaurants();

        verify(reviewRepository, times(1)).findTop5Restaurants();
    }
}
