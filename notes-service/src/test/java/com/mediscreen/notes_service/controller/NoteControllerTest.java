package com.mediscreen.notes_service.controller;

import com.mediscreen.notes_service.dto.NoteDTO;
import com.mediscreen.notes_service.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires purs pour le NoteController.
 */
@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteController noteController;

    private NoteDTO noteDTO;

    @BeforeEach
    void setUp() {
        noteDTO = new NoteDTO();
        noteDTO.setId("note_123");
        noteDTO.setPatientId(1);
        noteDTO.setNote("Le patient est stable.");
        noteDTO.setDate(LocalDateTime.now());
    }

    /**
     * Test pour vérifier que le contrôleur renvoie une liste de notes pour un patient donné.
     * Il s'assure que le service est appelé avec l'ID du patient et que la réponse est correcte.
     */
    @Test
    void getNotesForPatient_shouldReturnNoteList() {
        // GIVEN
        when(noteService.getNotesByPatientId(1)).thenReturn(Collections.singletonList(noteDTO));

        // WHEN
        ResponseEntity<List<NoteDTO>> response = noteController.getNotesForPatient(1);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getNote()).isEqualTo("Le patient est stable.");
        verify(noteService).getNotesByPatientId(1);
    }

    /**
     * Test pour vérifier que le contrôleur renvoie une liste vide si aucune note n'est trouvée pour un patient.
     * Il s'assure que le service est appelé avec l'ID du patient et que la réponse est correcte.
     */
    @Test
    void addNote_shouldCreateAndReturnNote() {
        // GIVEN
        // Le service reçoit un DTO et retourne ce même DTO (potentiellement avec un ID et une date)
        when(noteService.addNote(any(NoteDTO.class))).thenReturn(noteDTO);

        NoteDTO newNote = new NoteDTO();
        newNote.setPatientId(1);
        newNote.setNote("Nouvelle observation.");

        // WHEN
        ResponseEntity<NoteDTO> response = noteController.addNote(newNote);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("note_123"); // Vérifie que l'ID a été ajouté
        verify(noteService).addNote(any(NoteDTO.class));
    }
}
