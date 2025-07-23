package com.mediscreen.diabete_detection_app.gateway_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Classe de test pour le service de passerelle (Gateway Service).
 * Vérifie que les routes sont correctement configurées et accessibles.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayServiceApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	/**
	 * Test de démarrage du contexte de l'application.
	 * Vérifie que le contexte se charge correctement.
	 */
	@Test
	void whenPatientRequest_thenRouteIsFound() {
		webTestClient
				.get().uri("/patients/1")
				.exchange()
				// Vérifie que c'est une erreur serveur, ce qui prouve que la route a été trouvée
				.expectStatus().is5xxServerError();
	}

	/**
	 * Test de la route pour les notes d'un patient.
	 * Vérifie que la route est accessible et renvoie une erreur serveur.
	 */
	@Test
	void whenNotesRequest_thenRouteIsFound() {
		webTestClient
				.get().uri("/notes/patient/1")
				.exchange()
				.expectStatus().is5xxServerError();
	}

	/**
	 * Test de la route pour le rapport d'un patient.
	 * Vérifie que la route est accessible et renvoie une erreur serveur.
	 */
	@Test
	void whenReportRequest_thenRouteIsFound() {
		webTestClient
				.get().uri("/report/patient/1")
				.exchange()
				.expectStatus().is5xxServerError();
	}

	/**
	 * Test de la route pour les alertes d'un patient.
	 * Vérifie que la route est accessible et renvoie une erreur serveur.
	 */
	@Test
	void whenUnknownRequest_thenReturns404NotFound() {
		webTestClient
				.get().uri("/this/path/does/not/exist")
				.exchange()
				.expectStatus().isNotFound();
	}
}