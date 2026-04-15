package es.urjc.services;

import es.urjc.model.Review;
import es.urjc.model.User;
import es.urjc.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;

    public void deleteReview(long reviewId, User currentUser) {

        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();

            if (review.getAuthor().getId().equals(currentUser.getId())) {
                //Si coinciden los IDS los borramos
                reviewRepository.delete(review);
            } else {
                //Si no coinciden, lanzamos un error
                throw new IllegalArgumentException("No tienes permiso para borrar esta reseña.");
            }
        } else {
            throw new IllegalArgumentException("Reseña no existe.");
        }
    }

    public void saveReview(Review review) {

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("La valoración debe estar entre 1 y 5.");
        }

        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }

        reviewRepository.save(review);
    }

    public List<ReviewRepository.RestaurantStats> getTop5Restaurants() {
        return reviewRepository.findTop5Restaurants();
    }

    // Editar reseña
    public void editReview(Long reviewId, int newRating, String newComment, User currentUser) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        // Comprobamos que el usuario es el dueño de la reseña
        if (!review.getAuthor().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("No puedes editar una reseña que no es tuya.");
        }
        // Validamos los nuevos datos
        if (newRating < 1 || newRating > 5) {
            throw new IllegalArgumentException("La valoración debe estar entre 1 y 5.");
        }
        if (newComment == null || newComment.trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }

        // Guardamos
        review.setRating(newRating);
        review.setComment(newComment);
        reviewRepository.save(review);
    }
}
