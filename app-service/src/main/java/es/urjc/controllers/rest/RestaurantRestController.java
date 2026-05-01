package es.urjc.controllers.rest;

// =========================================================
// SARA
// =========================================================
// Controlador REST para restaurantes.
//
// Endpoints a implementar:
//
//   GET    /api/v1/restaurants              → lista paginada, público
//                                             Filtros opcionales como
//                                             parámetros de URL:
//                                             ?query=...&municipality=...&specialty=...
//   GET    /api/v1/restaurants/{id}         → detalle, público
//   POST   /api/v1/restaurants              → crear (solo ADMIN)
//   PUT    /api/v1/restaurants/{id}         → editar (solo ADMIN)
//   DELETE /api/v1/restaurants/{id}         → borrar (solo ADMIN)
//   GET    /api/v1/restaurants/{id}/image   → devuelve la imagen
//   PUT    /api/v1/restaurants/{id}/image   → subir/cambiar imagen (ADMIN)
//
// IMPORTANTE (rúbrica #24):
//   - Usa RestaurantDTO en respuestas, nunca la entidad Restaurant
//
// IMPORTANTE (rúbrica #21):
//   - Usa RestaurantService para toda la lógica.
//   - NO accedas a RestaurantRepository directamente desde aquí.
//
// La paginación usa los mismos parámetros que la web: ?page=0&size=10
// =========================================================

public class RestaurantRestController {
    // TODO (Persona B): implementar
}