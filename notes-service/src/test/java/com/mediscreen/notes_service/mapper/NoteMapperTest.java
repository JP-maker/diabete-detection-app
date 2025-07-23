package com.mediscreen.notes_service.mapper;

import com.mediscreen.notes_service.dto.NoteDTO;
import com.mediscreen.notes_service.model.Note;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NoteMapperTest {

    private final NoteMapper mapper = Mappers.getMapper(NoteMapper.class);

    /**
     * Test pour vérifier que le mapper NoteMapper convertit correctement
     * un objet Note en NoteDTO et vice versa.
     */
    @Test
    void shouldMapNoteToNoteDTO() {
        // GIVEN
        Note note = new Note();
        note.setId("id_1");
        note.setPatientId(10);
        note.setDate(LocalDateTime.now());
        note.setNote("Ceci est une note.");

        // WHEN
        NoteDTO dto = mapper.toDto(note);

        // THEN
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(note.getId());
        assertThat(dto.getPatientId()).isEqualTo(note.getPatientId());
        assertThat(dto.getDate()).isEqualTo(note.getDate());
        assertThat(dto.getNote()).isEqualTo(note.getNote());
    }

    /**
     *   Test pour vérifier que le mapper NoteMapper convertit correctement
     * un objet NoteDTO en Note.
     */
    @Test
    void shouldMapNoteDTOToNote() {
        // GIVEN
        NoteDTO dto = new NoteDTO();
        dto.setId("id_2");
        dto.setPatientId(20);
        dto.setDate(LocalDateTime.now());
        dto.setNote("Ceci est un DTO.");

        // WHEN
        Note entity = mapper.toEntity(dto);

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getPatientId()).isEqualTo(dto.getPatientId());
        assertThat(entity.getDate()).isEqualTo(dto.getDate());
        assertThat(entity.getNote()).isEqualTo(dto.getNote());
    }

    @Test
    void shouldHandleNullsGracefully() {
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
    }
}
