package es.urjc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class UtilityClient {

    private static final Logger log = LoggerFactory.getLogger(UtilityClient.class);

    private final RestClient restClient;

    public UtilityClient(@Value("${utility.service.url}") String utilityServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(utilityServiceUrl)
                .build();
    }

    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            restClient.post()
                    .uri("/api/v1/emails")
                    .body(Map.of("toEmail", toEmail, "firstName", firstName))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Welcome email requested for {}", toEmail);
        } catch (Exception e) {
            log.warn("Could not send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }
}