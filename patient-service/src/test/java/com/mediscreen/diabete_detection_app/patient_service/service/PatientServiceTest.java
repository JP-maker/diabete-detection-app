package com.mediscreen.diabete_detection_app.patient_service.service;

import com.mediscreen.diabete_detection_app.patient_service.dto.PatientDTO;
import com.mediscreen.diabete_detection_app.patient_service.exception.PatientNotFoundException;
import com.mediscreen.diabete_detection_app.patient_service.mapper.PatientMapper;
import com.mediscreen.diabete_detection_app.patient_service.model.Patient;
import com.mediscreen.diabete_detection_app.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la couche PatientService.
 * @ExtendWith(MockitoExtension.class) active l'utilisation des annotations Mockito.
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock // Crée un mock du repository
    private PatientRepository patientRepository;

    @Mock // Crée un mock du mapper
    private PatientMapper patientMapper;

    @InjectMocks // Crée une instance de PatientService et y injecte les mocks ci-dessus
    private PatientService patientService;

    private Patient patient;
    private PatientDTO patientDTO;

    /**
     * Méthode exécutée avant chaque test pour initialiser les objets nécessaires.
     * Ici, on crée un Patient et un PatientDTO pour les tests.
     */
    @BeforeEach
    void setUp() {
        patient = new Patient(1, "Jane", "Doe", LocalDate.of(1990, 2, 15), "F", "123 Street", "555-1234");
        patientDTO = new PatientDTO();
        patientDTO.setId(1);
        patientDTO.setPrenom("Jane");
        patientDTO.setNom("Doe");
    }

    /**
     * Test unitaire pour vérifier que le service PatientService peut récupérer un patient par son ID.
     * Il utilise Mockito pour simuler les interactions avec le repository et le mapper.
     */
    @Test
    void getPatientById_whenPatientExists_shouldReturnPatientDTO() {
        // GIVEN
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(patientDTO);

        // WHEN
        PatientDTO result = patientService.getPatientById(1);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("Doe");
        verify(patientRepository, times(1)).findById(1);
        verify(patientMapper, times(1)).toDto(patient);
    }

    /**
     * Test unitaire pour vérifier que le service PatientService lance une exception
     * lorsque l'on tente de récupérer un patient qui n'existe pas.
     */
    @Test
    void getPatientById_whenPatientDoesNotExist_shouldThrowException() {
        // GIVEN
        when(patientRepository.findById(99)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.getPatientById(99);
        });

        verify(patientRepository, times(1)).findById(99);
        verifyNoInteractions(patientMapper); // Le mapper ne doit jamais être appelé
    }

    /**
     * Test unitaire pour vérifier que le service PatientService peut ajouter un nouveau patient.
     * Il utilise Mockito pour simuler les interactions avec le repository et le mapper.
     */
    @Test
    void updatePatient_shouldReturnUpdatedDTO() {
        // GIVEN: Le DTO avec les nouvelles informations
        PatientDTO updatedInfo = new PatientDTO();
        updatedInfo.setId(1);
        updatedInfo.setPrenom("Janet"); // Le prénom a changé
        updatedInfo.setNom("Doe");
        updatedInfo.setDateDeNaissance(patient.getDateDeNaissance());
        updatedInfo.setGenre(patient.getGenre());

        // Configuration des mocks pour simuler le flux "charger puis mettre à jour"
        // 1. Quand le service demande le patient 1, on retourne l'entité originale.
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

        // 2. Quand le service sauvegarde l'entité (qui aura été modifiée), on retourne cette même entité.
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // 3. Quand le service demande de convertir l'entité finale en DTO, on retourne le DTO mis à jour.
        when(patientMapper.toDto(any(Patient.class))).thenReturn(updatedInfo);

        // WHEN: On appelle la méthode du service
        PatientDTO result = patientService.updatePatient(1, updatedInfo);

        // THEN: On vérifie les résultats
        assertThat(result).isNotNull();
        assertThat(result.getPrenom()).isEqualTo("Janet");

        // On vérifie que les bonnes méthodes des mocks ont été appelées
        verify(patientRepository).findById(1);
        verify(patientRepository).save(patient); // On peut même vérifier que c'est bien l'instance 'patient' qui est sauvegardée
        verify(patientMapper).toDto(patient);

        // On peut aussi vérifier que le champ de l'entité a bien été modifié avant la sauvegarde
        assertThat(patient.getPrenom()).isEqualTo("Janet");
    }

    /**
     * Test unitaire pour vérifier que le service PatientService lance une exception
     * lorsque l'on tente de mettre à jour un patient qui n'existe pas.
     */
    @Test
    void updatePatient_whenPatientDoesNotExist_shouldThrowException() {
        // GIVEN
        PatientDTO dto = new PatientDTO();
        // On dit au mock que findById retourne un Optional vide
        when(patientRepository.findById(99)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.updatePatient(99, dto);
        });

        // On vérifie que la sauvegarde n'est jamais appelée
        verify(patientRepository, never()).save(any(Patient.class));
    }
}
