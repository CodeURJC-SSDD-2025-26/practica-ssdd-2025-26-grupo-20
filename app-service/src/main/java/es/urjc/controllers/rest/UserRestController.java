package es.urjc.controllers.rest;

// =========================================================
// ALEXIS
// =========================================================
// Controlador REST para gestión de usuarios.
//
// Endpoints a implementar:
//
//   GET    /api/v1/users          → lista paginada (solo ADMIN)
//   GET    /api/v1/users/{id}     → detalle de un usuario
//   PUT    /api/v1/users/{id}     → editar usuario
//                                    (solo el propio usuario o ADMIN)
//   DELETE /api/v1/users/{id}     → borrar usuario
//                                    (solo el propio usuario o ADMIN)
//   GET    /api/v1/users/{id}/avatar → devuelve la imagen del avatar
//
// IMPORTANTE control de acceso (rúbrica #15):
//   - Un usuario normal solo puede editar/borrar SU PROPIO perfil
//   - El admin puede editar/borrar cualquiera
//   - Si un usuario intenta editar otro → 403 Forbidden
//
// IMPORTANTE (rúbrica #24):
//   - Usa UserDTO en todas las respuestas, nunca la entidad User
//   - El UserDTO NO lleva contraseña (rúbrica #25)
//
// Usa UserService para toda la lógica (no accedas al repositorio
// directamente desde aquí).
// =========================================================

public class UserRestController {
    // TODO (Persona A): implementar
}