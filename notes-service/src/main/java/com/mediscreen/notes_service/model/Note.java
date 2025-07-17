package com.mediscreen.notes_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Entité représentant une note du médecin, stockée dans une collection MongoDB.
 * L'annotation @Document spécifie le nom de la collection.
 */
@Document("notes")
@Data
public class Note {
    /**
     * Identifiant unique de la note, généré automatiquement par MongoDB.
     * Le type est String pour les _id de MongoDB.
     */
    @Id
    private String id;
    /**
     * Identifiant du patient auquel cette note est associée.
     * C'est le lien avec le microservice patient.
     */
    private Integer patientId;
    /**
     * Date et heure de la création de la note.
     */
    private LocalDateTime date;
    /**
     * Le contenu textuel de la note du médecin.
     */
    private String note;
}
