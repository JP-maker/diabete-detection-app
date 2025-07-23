package com.mediscreen.frontend_ui_service.controller;

import com.mediscreen.frontend_ui_service.dto.NoteDTO;
import com.mediscreen.frontend_ui_service.dto.PatientDTO;
import com.mediscreen.frontend_ui_service.proxy.NotesServiceProxy;
import com.mediscreen.frontend_ui_service.proxy.PatientServiceProxy;
import com.mediscreen.frontend_ui_service.proxy.ReportServiceProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour le UiController.
 * On utilise MockMvc pour simuler les requêtes GET/POST et vérifier les redirections,
 * tout en utilisant Mockito pour isoler le contrôleur de ses dépendances (proxies).
 */
@ExtendWith(MockitoExtension.class)
class UiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientServiceProxy patientServiceProxy;
    @Mock
    private NotesServiceProxy notesServiceProxy;
    @Mock
    private ReportServiceProxy reportServiceProxy;

    // Mocks pour les arguments des méthodes que l'on ne peut pas instancier facilement
    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UiController uiController;

    private PatientDTO patientDTO;
    private NoteDTO noteDTO;

    /**
     * Méthode exécutée avant chaque test pour initialiser les mocks et le MockMvc.
     * On crée un patient et une note de test pour les utiliser dans les tests.
     */
    @BeforeEach
    void setUp() {
        // Configuration de MockMvc pour tester le contrôleur en isolation
        mockMvc = MockMvcBuilders.standaloneSetup(uiController).build();

        patientDTO = new PatientDTO();
        patientDTO.setId(1);
        patientDTO.setNom("TestNom");
        patientDTO.setPrenom("TestPrenom");
        patientDTO.setDateDeNaissance(LocalDate.of(1990, 1, 1));

        noteDTO = new NoteDTO();
        noteDTO.setId("note1");
        noteDTO.setPatientId(1);
        noteDTO.setNote("Contenu de la note");
    }

    /**
     * Test pour vérifier que la page d'accueil redirige vers la liste des patients.
     */
    @Test
    void showPatientListPage_shouldReturnViewWithPatientList() throws Exception {
        // GIVEN
        when(patientServiceProxy.getAllPatients()).thenReturn(Collections.singletonList(patientDTO));

        // WHEN & THEN
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/list")) // On vérifie que la bonne vue est retournée
                .andExpect(model().attributeExists("patients")) // On vérifie que l'attribut "patients" existe
                .andExpect(model().attribute("patients", Collections.singletonList(patientDTO)));

        verify(patientServiceProxy).getAllPatients(); // On vérifie que le proxy a été appelé
    }

    /**
     * Test pour vérifier que la page d'accueil redirige vers la liste des patients.
     */
    @Test
    void showPatientDetailPage_shouldReturnViewWithDetails() throws Exception {
        // GIVEN
        when(patientServiceProxy.getPatientById(1)).thenReturn(patientDTO);
        when(notesServiceProxy.getNotesByPatientId(1)).thenReturn(Collections.singletonList(noteDTO));
        when(reportServiceProxy.getDiabetesReport(1)).thenReturn("Some report");

        // WHEN & THEN
        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/detail"))
                .andExpect(model().attributeExists("patient", "notes", "newNote", "diabetesReport"));

        verify(patientServiceProxy).getPatientById(1);
        verify(notesServiceProxy).getNotesByPatientId(1);
        verify(reportServiceProxy).getDiabetesReport(1);
    }

    /**
     * Test pour vérifier que la page d'accueil redirige vers la liste des patients.
     */
    @Test
    void showAddForm_shouldReturnAddView() throws Exception {
        mockMvc.perform(get("/patients/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/add"))
                .andExpect(model().attributeExists("patient"));
    }

    /**
     * Test pour vérifier que la page d'accueil redirige vers la liste des patients.
     */
    @Test
    void processAddPatient_whenValid_shouldRedirect() throws Exception {
        // GIVEN:
        // Le proxy est configuré pour ne rien faire (comportement par défaut des mocks pour les méthodes void).
        // On n'a PAS besoin de mocker le BindingResult pour le cas valide.

        // WHEN & THEN:
        // On simule une requête POST avec les attributs du formulaire.
        // .param() est utilisé pour simuler les champs envoyés par un formulaire HTML.
        mockMvc.perform(post("/patients/save")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("prenom", "TestPrenom")
                                .param("nom", "TestNom")
                                .param("dateDeNaissance", "1990-01-01")
                                .param("genre", "M")
                        // On peut omettre les champs optionnels
                )
                .andExpect(status().is3xxRedirection()) // On vérifie bien la redirection
                .andExpect(redirectedUrl("/patients")); // On vérifie l'URL de redirection

        // On vérifie que le proxy a bien été appelé
        verify(patientServiceProxy).addPatient(any(PatientDTO.class));
    }

    /**
     * Test pour vérifier que la page d'accueil redirige vers la liste des patients.
     * On teste le cas où le formulaire d'ajout de patient contient des erreurs de validation.
     */
    @Test
    void processAddPatient_whenInvalid_shouldReturnAddView() throws Exception {
        // GIVEN
        when(bindingResult.hasErrors()).thenReturn(true);

        // WHEN & THEN
        // Utilisation de l'appel direct à la méthode pour tester le cas d'erreur de validation
        String viewName = uiController.processAddPatient(patientDTO, bindingResult, model);
        assertThat(viewName).isEqualTo("patient/add");

        verify(patientServiceProxy, never()).addPatient(any(PatientDTO.class)); // Le proxy ne doit pas être appelé
    }

    /**
     * Test pour vérifier que la page d'accueil redirige vers la liste des patients.
     * On teste le cas où le formulaire d'ajout de note contient des erreurs de validation.
     */
    @Test
    void processAddNote_shouldRedirectToPatientDetail() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/patients/1/notes/add")
                        .flashAttr("newNote", noteDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/1"));

        // Utilisation d'ArgumentCaptor pour vérifier l'objet passé au proxy
        ArgumentCaptor<NoteDTO> noteCaptor = ArgumentCaptor.forClass(NoteDTO.class);
        verify(notesServiceProxy).addNote(noteCaptor.capture());
        assertThat(noteCaptor.getValue().getPatientId()).isEqualTo(1);
    }
}