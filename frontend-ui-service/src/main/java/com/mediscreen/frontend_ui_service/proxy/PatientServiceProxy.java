package com.mediscreen.frontend_ui_service.proxy;

import com.mediscreen.frontend_ui_service.dto.PatientDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Proxy qui gère toute la communication avec l'API back-end (via la Gateway).
 * Il encapsule la logique des appels REST et l'authentification.
 */
@Component
@Slf4j
public class PatientServiceProxy {

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

    /**
     * Récupère la liste complète des patients.
     * @return Une liste de patients, ou une liste vide en cas d'erreur.
     */
    public List<PatientDto> getAllPatients() {
        String url = gatewayUrl + "/patients";
        log.info("Appel de l'API pour récupérer tous les patients sur l'URL : {}", url);

        try {
            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
            // On utilise .exchange() car il permet de spécifier les en-têtes
            // ParameterizedTypeReference est nécessaire pour que RestTemplate sache
            // comment désérialiser le JSON en une List<Patient>.
            ResponseEntity<List<PatientDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            log.info("Appel API réussi. {} patients reçus.", response.getBody().size());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Erreur API lors de la récupération des patients. Statut : {}, Réponse : {}", e.getStatusCode(), e.getResponseBodyAsString());
            return Collections.emptyList(); // Renvoyer une liste vide pour ne pas faire planter l'UI
        }
    }

    /**
     * Récupère un patient par son ID.
     * @param id L'ID du patient à récupérer.
     */
    public PatientDto getPatientById(Integer id) {
        String url = gatewayUrl + "/patients/" + id;
        log.info("Appel de l'API pour récupérer le patient avec ID : {}", id);

        try {
            HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<PatientDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PatientDto.class
            );
            log.info("Appel API réussi. Patient récupéré : {}", response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Erreur API lors de la récupération du patient avec ID {}. Statut : {}, Réponse : {}", id, e.getStatusCode(), e.getResponseBodyAsString());
            return null; // Renvoyer null pour indiquer que le patient n'a pas été trouvé
        }
    }

    /**
     * Met à jour un patient existant en envoyant une requête PUT à l'API.
     * @param id L'identifiant du patient à mettre à jour.
     * @param patient L'objet patient contenant les nouvelles informations.
     */
    public void updatePatient(Integer id, PatientDto patient) {
        String url = gatewayUrl + "/patients/" + id;
        log.info("Appel de l'API pour mettre à jour le patient avec l'id {} sur l'URL : {}", id, url);

        try {
            // Pour une requête PUT, on a besoin des en-têtes et du corps de la requête (le patient)
            HttpEntity<PatientDto> requestEntity = new HttpEntity<>(patient, createAuthHeaders());

            restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class // On ne s'attend à aucune donnée en retour, juste un statut 200 OK.
            );
            log.info("Patient {} mis à jour avec succès.", id);
        } catch (HttpClientErrorException e) {
            log.error("Erreur API lors de la mise à jour du patient {}. Statut : {}, Réponse : {}", id, e.getStatusCode(), e.getResponseBodyAsString());
            // Dans une vraie application, on lèverait une exception ici pour informer l'utilisateur.
        }
    }
}