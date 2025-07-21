package com.mediscreen.frontend_ui_service.proxy;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class ReportServiceProxy {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gateway.url}")
    private String gatewayUrl;

    /**
     * Crée les en-têtes HTTP nécessaires pour l'authentification Basic Auth.
     * @return HttpHeaders avec l'en-tête "Authorization".
     */
    private HttpHeaders createAuthHeaders() {
        String auth = "user:password"; // Les identifiants pour se connecter au back-end
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    public String getDiabetesReport(Integer patientId) {
        String url = gatewayUrl + "/report/patient/" + patientId;
        log.info("Proxy: Récupération du rapport pour le patient {}", patientId);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createAuthHeaders()), String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Erreur proxy lors de la récupération du rapport", e);
            return "Erreur lors de la génération du rapport.";
        }
    }
}