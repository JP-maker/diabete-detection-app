package com.mediscreen.notes_service.service;

import com.mediscreen.notes_service.dto.NoteDTO;
import com.mediscreen.notes_service.mapper.NoteMapper;
import com.mediscreen.notes_service.model.Note;
import com.mediscreen.notes_service.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la couche NoteService.
 */
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NoteMapper noteMapper;

    @InjectMocks
    private NoteService noteService;

    private Note note;
    private NoteDTO noteDTO;

    @BeforeEach
    void setUp() {
        note = new Note();
        note.setId("note_abc");
        note.setPatientId(1);
        note.setNote("Observation du médecin.");
        note.setDate(LocalDateTime.now());

        noteDTO = new NoteDTO();
        noteDTO.setId("note_abc");
        noteDTO.setPatientId(1);
        noteDTO.setNote("Observation du médecin.");
    }

    /*
     * Test pour vérifier que le service retourne une liste de NoteDTOs
     * pour un patient donné.
     * On simule le comportement du repository et du mapper.
     */
    @Test
    void getNotesByPatientId_shouldReturnListOfNoteDTOs() {
        // GIVEN
        when(noteRepository.findByPatientIdOrderByDateDesc(1)).thenReturn(Collections.singletonList(note));
        when(noteMapper.toDto(note)).thenReturn(noteDTO);

        // WHEN
        List<NoteDTO> results = noteService.getNotesByPatientId(1);

        // THEN
        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPatientId()).isEqualTo(1);
        verify(noteRepository).findByPatientIdOrderByDateDesc(1);
        verify(noteMapper).toDto(note);
    }

    /*
     * Test pour vérifier que le service retourne une liste vide si aucune note n'est trouvée.
     */
    @Test
    void addNote_shouldSaveNoteAndReturnDTO() {
        // GIVEN:
        // 1. Le DTO que le client envoie (sans ID ni date)
        NoteDTO newNoteDto = new NoteDTO();
        newNoteDto.setPatientId(2);
        newNoteDto.setNote("Nouvelle note de test.");

        // 2. L'entité que le mapper est censé créer à partir du DTO
        Note noteToSave = new Note();
        noteToSave.setPatientId(2);
        noteToSave.setNote("Nouvelle note de test.");

        // 3. L'entité que le repository est censé retourner après sauvegarde (avec ID et date)
        Note savedNote = new Note();
        savedNote.setId("note_xyz");
        savedNote.setPatientId(2);
        savedNote.setNote("Nouvelle note de test.");
        savedNote.setDate(LocalDateTime.now());

        // 4. Le DTO final que le mapper est censé créer à partir de l'entité sauvegardée
        NoteDTO finalDto = new NoteDTO();
        finalDto.setId("note_xyz");
        finalDto.setPatientId(2);
        finalDto.setNote("Nouvelle note de test.");
        finalDto.setDate(savedNote.getDate());


        // Configuration des mocks
        when(noteMapper.toEntity(any(NoteDTO.class))).thenReturn(noteToSave);
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);
        when(noteMapper.toDto(savedNote)).thenReturn(finalDto);

        // WHEN:
        // On appelle la méthode et on capture son résultat
        NoteDTO result = noteService.addNote(newNoteDto);

        // THEN:
        // 1. Vérifier le résultat retourné par le service
        assertThat(result).isNotNull(); // <--- PREMIÈRE ASSERTION IMPORTANTE
        assertThat(result.getId()).isEqualTo("note_xyz");
        assertThat(result.getDate()).isNotNull();

        // 2. (Optionnel mais recommandé) Vérifier l'objet passé à la méthode save
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(noteCaptor.capture());

        Note capturedNote = noteCaptor.getValue();
        assertThat(capturedNote).isNotNull();
        assertThat(capturedNote.getPatientId()).isEqualTo(2);
        assertThat(capturedNote.getNote()).isEqualTo("Nouvelle note de test.");
        assertThat(capturedNote.getId()).isNull(); // L'ID doit être null avant la sauvegarde
        assertThat(capturedNote.getDate()).isNull(); // La date doit être null avant la sauvegarde
        // 3. Vérifier que le mapper a été appelé pour convertir le DTO en entité
        verify(noteMapper).toEntity(newNoteDto);
        // 4. Vérifier que le mapper a été appelé pour convertir l'entité sauvegardée en DTO
        verify(noteMapper).toDto(savedNote);

    }
}
