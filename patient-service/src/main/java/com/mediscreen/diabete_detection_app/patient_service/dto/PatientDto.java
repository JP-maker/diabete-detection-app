package com.mediscreen.diabete_detection_app.patient_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) pour représenter un patient.
 * Utilisé pour transférer les données entre le client et le serveur.
 * Contient des annotations de validation pour assurer l'intégrité des données.
 */
@Data
public class PatientDto {

    private Integer id;

    @NotBlank(message = "Le prénom ne peut pas être vide.")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères.")
    private String prenom;

    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères.")
    private String nom;

    @Past(message = "La date de naissance doit être dans le passé.")
    private LocalDate dateDeNaissance;

    @NotBlank(message = "Le genre doit être spécifié ('M' ou 'F').")
    private String genre;

    private String adressePostale;

    private String numeroDeTelephone;
}
