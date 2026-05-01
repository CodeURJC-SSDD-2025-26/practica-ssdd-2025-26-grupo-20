package es.urjc.security;

// =========================================================
// ALEXIS
// =========================================================
// Filtro que intercepta cada petición a /api/v1/** y comprueba
// si lleva un token JWT válido en la cabecera Authorization.
//
// Formato esperado de la cabecera:
//   Authorization: Bearer <token>
//
// Lo que debe hacer este filtro:
//   1. Leer la cabecera Authorization
//   2. Si no hay token → dejar pasar (Spring Security decidirá
//      si la ruta es pública o no)
//   3. Si hay token → validarlo con JwtUtils
//   4. Si es válido → crear un UsernamePasswordAuthenticationToken
//      y meterlo en el SecurityContextHolder
//   5. Si no es válido → no meter nada (la petición llegará
//      como anónima y Spring Security la rechazará si la ruta
//      requiere autenticación)
//
// Extiende: OncePerRequestFilter
// =========================================================

public class JwtFilter {
    // TODO : implementar
}