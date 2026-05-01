package es.urjc.security;

// =========================================================
// ALEXIS
// =========================================================
// Clase de utilidad para generar y validar tokens JWT.
//
// Dependencias necesarias en pom.xml (ya añadidas/revisarlas):
//   <dependency>
//     <groupId>io.jsonwebtoken</groupId>
//     <artifactId>jjwt-api</artifactId>
//     <version>0.12.6</version>
//   </dependency>
//   <dependency>
//     <groupId>io.jsonwebtoken</groupId>
//     <artifactId>jjwt-impl</artifactId>
//     <version>0.12.6</version>
//     <scope>runtime</scope>
//   </dependency>
//   <dependency>
//     <groupId>io.jsonwebtoken</groupId>
//     <artifactId>jjwt-jackson</artifactId>
//     <version>0.12.6</version>
//     <scope>runtime</scope>
//   </dependency>
//
// Métodos que debes implementar:
//   - generateToken(String username, List<String> roles) → String
//   - validateToken(String token) → boolean
//   - getUsernameFromToken(String token) → String
//   - getRolesFromToken(String token) → List<String>
//
// La clave secreta léela de application.properties:
//   jwt.secret=tuClaveSecretaMuyLarga
//   jwt.expiration=86400000  (24h en ms)
// =========================================================

public class JwtUtils {
    // TODO (Persona A): implementar
}