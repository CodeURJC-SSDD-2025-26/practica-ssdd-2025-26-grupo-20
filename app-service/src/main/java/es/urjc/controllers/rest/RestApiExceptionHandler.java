package es.urjc.controllers.rest;

// =========================================================
// RODRI
// =========================================================
// Manejador global de errores para la API REST.
// Rúbrica #18: cuando falla algo en la API REST, la respuesta
// debe ser JSON, NO HTML.
//
// Sin esto, Spring devuelve una página HTML de error cuando
// por ejemplo no se encuentra un recurso o hay un 403.
// Eso rompe cualquier cliente de la API (Postman, app móvil).
//
// Lo que debe hacer:
//   - Capturar excepciones comunes (404, 403, 400, 500)
//   - Devolver siempre JSON con este formato:
//     {
//       "status": 404,
//       "error": "Not Found",
//       "message": "Restaurant not found"
//     }
//
// Anota la clase con @RestControllerAdvice
// Limita su alcance SOLO a los RestControllers con:
//   @RestControllerAdvice(basePackages = "es.urjc.controllers.rest")
// Así no interfiere con los errores de la web (que usan HTML).
//
// Excepciones a capturar como mínimo:
//   - IllegalArgumentException → 400
//   - AccessDeniedException → 403
//   - NoSuchElementException → 404
//   - Exception (genérica) → 500
// =========================================================

public class RestApiExceptionHandler {
    // TODO (Persona D): implementar
}