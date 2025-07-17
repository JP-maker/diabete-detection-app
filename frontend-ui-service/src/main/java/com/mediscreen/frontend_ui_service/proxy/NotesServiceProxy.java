package com.mediscreen.frontend_ui_service.proxy;

import com.mediscreen.frontend_ui_service.dto.NoteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class NotesServiceProxy { // On peut créer une classe abstraite pour le code commun

    private final RestTemplate restTemplate = new RestTemplate();

    // Injection de l'URL de la gateway depuis application.properties
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

    public List<NoteDTO> getNotesByPatientId(Integer patientId) {
        String url = gatewayUrl + "/notes/patient/" + patientId;
        log.info("Proxy: Récupération des notes pour le patient {} sur l'URL {}", patientId, url);
        try {
            ResponseEntity<List<NoteDTO>> response = restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createAuthHeaders()), new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (Exception e) {
            log.error("Erreur proxy lors de la récupération des notes pour le patient {}", patientId, e);
            return Collections.emptyList();
        }
    }

    public void addNote(NoteDTO note) {
        String url = gatewayUrl + "/notes";
        log.info("Proxy: Ajout d'une note pour le patient {}", note.getPatientId());
        try {
            HttpEntity<NoteDTO> request = new HttpEntity<>(note, createAuthHeaders());
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            log.error("Erreur proxy lors de l'ajout de la note", e);
        }
    }
}
