package com.mediscreen.report_service.service;

import com.mediscreen.report_service.dto.NoteDTO;
import com.mediscreen.report_service.dto.PatientDTO;
import com.mediscreen.report_service.proxy.NotesServiceProxy;
import com.mediscreen.report_service.proxy.PatientServiceProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour le ReportService.
 * L'objectif est de valider la logique de l'algorithme de calcul du risque
 * en isolant le service de ses dépendances (les proxies Feign).
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private PatientServiceProxy patientProxy;

    @Mock
    private NotesServiceProxy notesProxy;

    @InjectMocks
    private ReportService reportService;

    // --- Fonctions utilitaires pour créer des données de test ---

    private PatientDTO createPatient(String genre, int age) {
        PatientDTO patient = new PatientDTO();
        patient.setGenre(genre);
        patient.setDateDeNaissance(LocalDate.now().minusYears(age));
        patient.setPrenom("Test");
        patient.setNom("Patient");
        return patient;
    }

    private List<NoteDTO> createNotes(String... notesContent) {
        return Arrays.stream(notesContent)
                .map(content -> {
                    NoteDTO note = new NoteDTO();
                    note.setNote(content);
                    return note;
                })
                .toList();
    }

    // --- Tests pour chaque niveau de risque ---

    /*
     * Tests pour les différents niveaux de risque :
     * - None
     * - Borderline
     * - In Danger
     * - Early Onset
     */

    /**
     * Tests pour le niveau None :
     * - Moins de 3 déclencheurs pour les patients de plus de 30 ans.
     * - Moins de 3 déclencheurs pour les patients de moins de 30 ans.
     */
    @Test
    void generateReport_whenNoTriggers_shouldReturnNone() {
        // GIVEN
        PatientDTO patient = createPatient("M", 40);
        List<NoteDTO> notes = createNotes("Le patient va bien.", "Aucun symptôme notable.");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        assertThat(report).contains("Patient: Test Patient (age 40) son risque de diabète: Aucun risque");
    }

    /*
     * Tests pour le niveau Borderline :
     * - Moins de 3 déclencheurs pour les patients de plus de 30 ans.
     */
    @Test
    void generateReport_whenPatientIsOver30With3Triggers_shouldReturnBorderline() {
        // GIVEN
        PatientDTO patient = createPatient("F", 55);
        List<NoteDTO> notes = createNotes("Le patient est Fumeur.", "Son Poids a augmenté.", "Microalbumine élevée.");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        assertThat(report).contains("Patient: Test Patient (age 55) son risque de diabète: Risque limité");
    }

    /*
     * Tests pour le niveau In Danger :
     * - 3 déclencheurs pour les patients de moins de 30 ans.
     * - 4 déclencheurs pour les patients de plus de 30 ans.
     */
    @Test
    void generateReport_whenMaleUnder30With3Triggers_shouldReturnInDanger() {
        // GIVEN
        PatientDTO patient = createPatient("M", 25);
        List<NoteDTO> notes = createNotes("Hémoglobine A1C anormale", "Présence d'Anticorps", "Le patient a des Vertiges");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        assertThat(report).contains("Patient: Test Patient (age 25) son risque de diabète: Apparition précoce");
    }

    /* * Tests pour le niveau In Danger :
     * - 4 déclencheurs pour les femmes de moins de 30 ans.
     * - 6 déclencheurs pour les patients de plus de 30 ans.
     */
    @Test
    void generateReport_whenFemaleUnder30With4Triggers_shouldReturnInDanger() {
        // GIVEN
        PatientDTO patient = createPatient("F", 28);
        List<NoteDTO> notes = createNotes("Hémoglobine A1C", "Anticorps", "Vertiges", "Rechute");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        assertThat(report).contains("Test Patient (age 28) son risque de diabète: Danger");
    }

    /*
     * Tests pour le niveau Early Onset :
     * - 5 déclencheurs pour les patients de moins de 30 ans.
     * - 6 déclencheurs pour les patients de plus de 30 ans.
     */
    @Test
    void generateReport_whenPatientOver30With6Triggers_shouldReturnInDanger() {
        // GIVEN
        PatientDTO patient = createPatient("M", 45);
        List<NoteDTO> notes = createNotes("Taille", "Poids", "Fumeur", "Anormal", "Cholestérol", "Vertiges");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        assertThat(report).contains("Patient: Test Patient (age 45) son risque de diabète: Danger");
    }

    /*
     * Tests pour le niveau Early Onset :
     * - 5 déclencheurs pour les patients de moins de 30 ans.
     * - 8 déclencheurs pour les patients de plus de 30 ans.
     */
    @Test
    void generateReport_whenMaleUnder30With5Triggers_shouldReturnEarlyOnset() {
        // GIVEN
        PatientDTO patient = createPatient("M", 29);
        List<NoteDTO> notes = createNotes("Hémoglobine A1C", "Microalbumine", "Taille", "Poids", "Fumeur");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        assertThat(report).contains("Patient: Test Patient (age 29) son risque de diabète: Apparition précoce");
    }

    /**
     * Tests pour le niveau Early Onset :
     * - 5 déclencheurs pour les femmes de moins de 30 ans.
     * - 8 déclencheurs pour les patients de plus de 30 ans.
     */
    @Test
    void generateReport_whenFemaleUnder30With7Triggers_shouldReturnEarlyOnset() {
        // GIVEN
        PatientDTO patient = createPatient("F", 20);
        List<NoteDTO> notes = createNotes("Hémoglobine A1C", "Microalbumine", "Taille", "Poids", "Fumeur", "Anormal", "Cholestérol");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        assertThat(report).contains("Patient: Test Patient (age 20) son risque de diabète: Apparition précoce");
    }

    /**
     * Tests pour le niveau Early Onset :
     * - 5 déclencheurs pour les patients de moins de 30 ans.
     * - 8 déclencheurs pour les patients de plus de 30 ans.
     */
    @Test
    void generateReport_whenPatientOver30With8Triggers_shouldReturnEarlyOnset() {
        // GIVEN
        PatientDTO patient = createPatient("F", 60);
        List<NoteDTO> notes = createNotes("Hémoglobine A1C", "Microalbumine", "Taille", "Poids", "Fumeur", "Anormal", "Cholestérol", "Vertiges");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        assertThat(report).contains("Patient: Test Patient (age 60) son risque de diabète: Apparition précoce");
    }

    /**
     * Tests pour le niveau Early Onset :
     * - 5 déclencheurs pour les patients de moins de 30 ans.
     * - 8 déclencheurs pour les patients de plus de 30 ans.
     */
    @Test
    void countTriggers_shouldBeCaseInsensitiveAndCountUniques() {
        // GIVEN: Un cas complexe pour valider le comptage
        // "Fumeur" est présent deux fois, mais ne doit compter que pour 1.
        // "poids" est en minuscule.
        PatientDTO patient = createPatient("M", 40);
        List<NoteDTO> notes = createNotes("Le patient est Fumeur.", "Son poids est stable. Il est aussi Fumeur.");

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(notesProxy.getNotesForPatient(1)).thenReturn(notes);

        // WHEN
        String report = reportService.generateDiabetesReport(1);

        // THEN
        // 2 déclencheurs (Fumeur, Poids) et plus de 30 ans -> Borderline
        assertThat(report).contains("Patient: Test Patient (age 40) son risque de diabète: Risque limité");
    }
}