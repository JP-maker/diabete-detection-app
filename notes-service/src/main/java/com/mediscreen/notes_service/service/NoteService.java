package com.mediscreen.notes_service.service;

import com.mediscreen.notes_service.dto.NoteDTO;
import com.mediscreen.notes_service.mapper.NoteMapper;
import com.mediscreen.notes_service.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoteService {

    @Autowired private NoteRepository noteRepository;
    @Autowired private NoteMapper noteMapper;

    public List<NoteDTO> getNotesByPatientId(Integer patientId) {
        log.info("Récupération des notes pour le patient ID : {}", patientId);
        return noteRepository.findByPatientIdOrderByDateDesc(patientId).stream()
                .map(noteMapper::toDto)
                .collect(Collectors.toList());
    }

    public NoteDTO addNote(NoteDTO noteDTO) {
        log.info("Ajout d'une nouvelle note pour le patient ID : {}", noteDTO.getPatientId());
        noteDTO.setId(null);
        noteDTO.setDate(LocalDateTime.now()); // On s'assure que la date est celle du moment de l'ajout
        var noteToSave = noteMapper.toEntity(noteDTO);
        var savedNote = noteRepository.save(noteToSave);
        log.info("Note créée avec succès avec l'ID : {}", savedNote.getId());
        return noteMapper.toDto(savedNote);
    }
}
