package es.urjc.utilityservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emails")
public class EmailRestController {

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<Void> sendEmail(@RequestBody EmailRequestDTO request) {
        emailService.sendWelcomeEmail(request.getToEmail(), request.getFirstName());
        return ResponseEntity.ok().build();
    }
}