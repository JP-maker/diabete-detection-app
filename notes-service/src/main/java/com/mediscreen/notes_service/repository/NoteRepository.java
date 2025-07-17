package com.mediscreen.notes_service.repository;

import com.mediscreen.notes_service.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository Spring Data pour l'entité Note.
 * Fournit les opérations CRUD et permet de définir des requêtes personnalisées.
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    /**
     * Trouve toutes les notes associées à un identifiant de patient spécifique.
     * Spring Data MongoDB génère l'implémentation de cette méthode automatiquement.
     * @param patientId L'ID du patient.
     * @return Une liste de notes.
     */
    List<Note> findByPatientIdOrderByDateDesc(Integer patientId);
}