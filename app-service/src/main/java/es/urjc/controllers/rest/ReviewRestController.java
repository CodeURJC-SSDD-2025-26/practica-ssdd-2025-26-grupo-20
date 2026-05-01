package es.urjc.controllers.rest;

// =========================================================
// SARA
// =========================================================
// Controlador REST para reseñas.
//
// Endpoints a implementar:
//
//   GET    /api/v1/restaurants/{id}/reviews  → reseñas de un restaurante
//   POST   /api/v1/restaurants/{id}/reviews  → crear reseña (usuario autenticado)
//   PUT    /api/v1/reviews/{id}              → editar reseña
//   DELETE /api/v1/reviews/{id}              → borrar reseña
//
// IMPORTANTE control de acceso (rúbrica #15):
//   - Solo el autor de la reseña puede editarla o borrarla
//   - El admin puede borrar cualquiera
//   - Si otro usuario intenta editar/borrar → 403 Forbidden
//
// IMPORTANTE (rúbrica #24):
//   - Usa ReviewDTO en respuestas, nunca la entidad Review
//
// IMPORTANTE (rúbrica #21):
//   - Usa ReviewService para toda la lógica.
//   - NO accedas a ReviewRepository directamente desde aquí.
//
// Validaciones (rúbrica #10):
//   - rating debe estar entre 1 y 5
//   - comment no puede estar vacío
//   - Devuelve 400 con mensaje de error si falla la validación
// =========================================================

public class ReviewRestController {
    // TODO (Persona B): implementar
}