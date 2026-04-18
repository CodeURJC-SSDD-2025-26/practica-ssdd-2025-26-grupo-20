package es.urjc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    /**
     * Envía un correo de bienvenida al usuario recién registrado.
     */
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("¡Bienvenido/a a Celis&Ud!");
            message.setText(
                "Hola, " + firstName + "!\n\n" +
                "Tu cuenta en Celis&Ud ha sido creada correctamente.\n\n" +
                "Ya puedes explorar restaurantes, escribir reseñas y crear listas de tus lugares favoritos.\n\n" +
                "¡Esperamos que disfrutes la experiencia!\n\n" +
                "— El equipo de Celis&Ud"
            );
            mailSender.send(message);
        } catch (Exception e) {
            // Si el envío falla (p.ej. credenciales incorrectas), el registro
            // continúa igualmente y no se muestra ningún error al usuario.
            System.err.println("[EmailService] Error al enviar correo de bienvenida a " + toEmail + ": " + e.getMessage());
        }
    }
}