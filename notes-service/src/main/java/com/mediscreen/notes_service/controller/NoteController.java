package com.mediscreen.notes_service.controller;

import com.mediscreen.notes_service.dto.NoteDTO;
import com.mediscreen.notes_service.service.NoteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des notes des patients.
 */
@RestController
@RequestMapping("/notes")
@Slf4j
public class NoteController {

    @Autowired private NoteService noteService;

    /**
     * Récupère toutes les notes d'un patient donné.
     * @param patientId L'ID du patient.
     * @return Une {@link ResponseEntity} avec la liste des {@link NoteDTO} et un statut 200 OK.
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<NoteDTO>> getNotesForPatient(@PathVariable Integer patientId) {
        log.info("Requête API pour récupérer les notes du patient ID : {}", patientId);
        List<NoteDTO> notes = noteService.getNotesByPatientId(patientId);
        return ResponseEntity.ok(notes);
    }

    /**
     * Ajoute une nouvelle note pour un patient.
     * @param noteDTO Le DTO contenant les informations de la note.
     * @return Une {@link ResponseEntity} avec le {@link NoteDTO} créé et un statut 201 Created.
     */
    @PostMapping
    public ResponseEntity<NoteDTO> addNote(@Valid @RequestBody NoteDTO noteDTO) {
        log.info("Requête API pour ajouter une note pour le patient ID : {}", noteDTO.getPatientId());
        NoteDTO savedNote = noteService.addNote(noteDTO);
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }
}
