package com.mediscreen.notes_service.mapper;

import com.mediscreen.notes_service.dto.NoteDTO;
import com.mediscreen.notes_service.model.Note;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteDTO toDto(Note note);
    Note toEntity(NoteDTO noteDTO);
}
