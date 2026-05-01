package es.urjc.controllers.rest;

// =========================================================
// ALEXIS
// =========================================================
// Controlador REST para autenticación.
// Todas las URLs de este controller son públicas (no requieren
// token) — configúralo así en SecurityConfig.
//
// Endpoints a implementar:
//
//   POST /api/v1/auth/login
//     Body: { "username": "...", "password": "..." }
//     Respuesta 200: { "token": "eyJ..." }
//     Respuesta 401: si las credenciales son incorrectas
//
//   POST /api/v1/auth/signup
//     Body: { "firstName": "...", "lastName": "...",
//             "email": "...", "username": "...", "password": "..." }
//     Respuesta 201: UserDTO del usuario creado
//     Cabecera Location: /api/v1/users/{id}
//     Respuesta 400: si falla validación (username duplicado, etc.)
//     IMPORTANTE: llama a utilityClient.sendWelcomeEmail() igual
//     que hace UserController.processSignup()
//
// Usa UserService para registrar y RepositoryUserDetailsService
// para autenticar. Genera el token con JwtUtils.
// =========================================================

public class AuthRestController {
    // TODO : implementar
}