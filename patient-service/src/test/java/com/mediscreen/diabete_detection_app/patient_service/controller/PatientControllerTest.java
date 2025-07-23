package com.mediscreen.diabete_detection_app.patient_service.controller;


import com.mediscreen.diabete_detection_app.patient_service.dto.PatientDTO;
import com.mediscreen.diabete_detection_app.patient_service.exception.PatientNotFoundException;
import com.mediscreen.diabete_detection_app.patient_service.model.Patient;
import com.mediscreen.diabete_detection_app.patient_service.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Classe de test unitaire pour le contrôleur PatientController.
 * Utilise Mockito pour simuler le service PatientService.
 */
@ExtendWith(MockitoExtension.class) // Active Mockito
class PatientControllerTest {

    @Mock // On crée un mock du service
    private PatientService patientService;

    @InjectMocks // On crée une instance du contrôleur et on y injecte le mock
    private PatientController patientController;

    private PatientDTO patientDTO;

    /**
     * Méthode exécutée avant chaque test pour initialiser un PatientDTO de base.
     * Cela permet d'éviter la duplication de code dans chaque test.
     */
    @BeforeEach
    void setUp() {
        patientDTO = new PatientDTO();
        patientDTO.setId(1);
        patientDTO.setPrenom("John");
        patientDTO.setNom("Doe");
        patientDTO.setDateDeNaissance(LocalDate.of(1980, 1, 1));
        patientDTO.setGenre("M");
    }

    /**
     * Test pour vérifier que la méthode getAllPatients du contrôleur
     * retourne une liste de PatientDTO.
     */
    @Test
    void getAllPatients_shouldReturnPatientDTOList() {
        // GIVEN
        // On dit au mock du SERVICE ce qu'il doit retourner
        when(patientService.getAllPatients()).thenReturn(Collections.singletonList(patientDTO));

        // WHEN
        // On appelle la méthode du contrôleur qui retourne maintenant une ResponseEntity<List<PatientDTO>>
        ResponseEntity<List<PatientDTO>> response = patientController.getAllPatients();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get(0).getNom()).isEqualTo("Doe");
        verify(patientService).getAllPatients(); // On vérifie que le SERVICE a bien été appelé
    }

    /**
     * Test pour vérifier que la méthode getPatientById du contrôleur
     * retourne un PatientDTO spécifique.
     */
    @Test
    void getPatientById_whenPatientExists_shouldReturnPatient() {
        // GIVEN
        when(patientService.getPatientById(1)).thenReturn(patientDTO);

        // WHEN
        ResponseEntity<PatientDTO> response = patientController.getPatientById(1);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1);
    }

    /**
     * Test pour vérifier que la méthode getPatientById du contrôleur
     * lance une exception si le patient n'existe pas.
     */
    @Test
    void getPatientById_whenPatientNotFound_shouldThrowException() {
        // GIVEN
        when(patientService.getPatientById(99)).thenThrow(new PatientNotFoundException("Patient non trouvé"));

        // WHEN & THEN
        // On vérifie que l'exception est bien propagée (ce qui donnera un 404 grâce à @ResponseStatus)
        assertThrows(PatientNotFoundException.class, () -> {
            patientController.getPatientById(99);
        });
    }

    /**
     * Test pour vérifier que la méthode addPatient du contrôleur
     * crée un nouveau patient et retourne le PatientDTO créé.
     */
    @Test
    void addPatient_shouldCreateAndReturnPatient() {
        // GIVEN
        when(patientService.addPatient(any(PatientDTO.class))).thenReturn(patientDTO);
        PatientDTO newPatient = new PatientDTO(); // Le DTO sans ID envoyé par le client
        newPatient.setNom("Doe");

        // WHEN
        ResponseEntity<PatientDTO> response = patientController.addPatient(newPatient);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1);
    }

    /**
     * Test pour vérifier que la méthode updatePatient du contrôleur
     * met à jour un patient existant et retourne le PatientDTO mis à jour.
     */
    @Test
    void updatePatient_shouldUpdateAndReturnPatient() {
        // GIVEN
        when(patientService.updatePatient(eq(1), any(PatientDTO.class))).thenReturn(patientDTO);

        // WHEN
        ResponseEntity<PatientDTO> response = patientController.updatePatient(1, patientDTO);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNom()).isEqualTo("Doe");
    }

    /**
     * Test pour vérifier que la méthode updatePatient du contrôleur
     * lance une exception si le patient à mettre à jour n'existe pas.
     */
    @Test
    void deletePatient_shouldReturnNoContent() {
        // GIVEN
        // doNothing() est implicite pour les méthodes void, mais on peut l'écrire pour la clarté.

        // WHEN
        ResponseEntity<Void> response = patientController.deletePatient(1);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(patientService).deletePatient(1); // On vérifie que l'appel a bien eu lieu
    }
}